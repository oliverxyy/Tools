#!/usr/bin/env python
# coding=utf-8

import smtplib
import os
import zipfile
import traceback
from email.mime.text import MIMEText
from email.header import Header
from email.mime.multipart import MIMEMultipart
from email.mime.image import MIMEImage
# title为test的时候可能会被某些邮件系统拒收（应该是触发了系统退信机制）


class IMail(object):
    def __init__(self, sender, password, port=25, timeout=10000, attachment=None, charset='utf-8'):
        # default port: 25
        # default timeout: 10s
        # default charset: utf-8
        self.server = 'smtp.' + sender[sender.index('@')+1:]
        self.port = port
        self.sender = sender
        self.password = password
        self.attachment = attachment
        self.charset = charset
        self.instance = smtplib.SMTP(timeout=timeout)
        if self.server == "smtp.gmail.com":
            self.port = 465

    def send_mail(self, to_addrs, title, content, send_type='plain', filepath=''):
        # to_addrs: str/list
        # send_type: plain/html/mixed
        # mixed时可以自定义content,dict类型,格式{send_type:content},如{"html":"<html>test</html>","plain":"test"}
        # success: return True
        # failed: return list[False, exception]

        #detect Exception
        self.throw_exception(send_type, filepath)
        filepath = self.zip_folder(filepath)
        msg = self.get_msg(send_type, content, filepath)
        msg['Subject'] = Header(title, self.charset)
        msg['From'] = self.sender
        msg['To'] = to_addrs
        try:
            self.instance.connect(self.server, self.port)
            self.instance.login(self.sender, self.password)
            self.instance.sendmail(self.sender, to_addrs, msg.as_string())
            self.instance.close()
            return True
        except BaseException, e:
            return False, e

    def get_msg(self, send_type, content, filepath):
        if filepath != '':
            send_type='mixed'
        if send_type == 'plain' or send_type == 'html':
            msg = MIMEText(content, _subtype=send_type, _charset=self.charset)
            return msg
        elif send_type == 'mixed':
            from email.mime.multipart import MIMEMultipart
            msg = MIMEMultipart()
            att = MIMEText(open(filepath, 'rb').read(), 'base64', self.charset)
            att['Content-Type'] = 'application/octet-stream'
            att['Content-Disposition'] = 'attachment; filename="'+filepath+'"'
            if type(content) == dict:
                for k, v in content.iteritems():
                    msg.attach(MIMEText(v, _subtype=k, _charset=self.charset))
            msg.attach(att)
            return msg

    def throw_exception(self, send_type, filepath):
        type_list = {'plain','html','mixed'}
        if send_type not in type_list:
            raise Exception("send_type should be plain, html or mixed!")
        if not os.path.exists(filepath):
            raise Exception("the filepath is not exist!")

    def zip_folder(self, filepath):
        if os.path.isdir(filepath):
            if filepath[len(filepath)-1] != '/':
                filepath = filepath + '/'
            file = zipfile.ZipFile(os.path.dirname(filepath)+".zip", 'w', zipfile.ZIP_DEFLATED)
            for dirpath, dirnames, filenames in os.walk(filepath):
                for filename in filenames:
                    file.write(os.path.join(dirpath, filename))
            file.close()
            return file.filename
        return filepath



