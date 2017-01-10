#!/usr/bin/env python
# coding=utf-8
import time
import os
import math
from imail.imail import IMail
import re


class Review(object):
    def __init__(self, path, sender, password, to_addrs, mail_title='开始复习', send_type='mixed'):
        # path: note directory
        self.path = path
        self.sender = sender
        self.password = password
        self.to_addrs = to_addrs
        self.mail_title = mail_title
        self.send_type = send_type
        self.list = {}
        if path[len(path)-1] != '/':
            self.path = ''.join([path, '/'])

    @staticmethod
    def get_current_date():
        return time.strftime('%Y%m%d', time.localtime(time.time()))

    @staticmethod
    def get_period(n):
        return 3 * (2 ** n)

    def is_ontime(self, hour):
        n = 0
        while hour > self.get_period(n):
            n += 1
        # print self.get_period(n) - hour
        return self.get_period(n) - hour < 0.5

    def get_hours_list(self):
        path = self.path
        pattern = re.compile(r'\d')
        for i in os.listdir(path):
            if pattern.match(i):
                # print i
                i = i[:i.rfind('.')]
                self.list[i] = round((time.time()-time.mktime(time.strptime(i, '%Y%m%d')))/(60*60))

    def start(self):
        while True:
            self.get_hours_list()
            for filename, hour in self.list.iteritems():
                if self.is_ontime(hour):
                    IMail(self.sender, self.password).send_mail(self.to_addrs, self.mail_title, "",
                                                                send_type=self.send_type, filepath=self.path + filename)
            time.sleep(60 * 60)  # check by hours





