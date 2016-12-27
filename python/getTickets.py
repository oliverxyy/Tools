#!/usr/bin/env python
# coding=utf-8

import ssl
import urllib2
import json
import sys
import functools
import time
import smtplib
from email.mime.text import MIMEText

ssl._create_default_https_context = ssl._create_unverified_context


def get_tickets(date):
    url = "https://kyfw.12306.cn/otn/leftTicket/queryZ?leftTicketDTO.train_date=%s\
&leftTicketDTO.from_station=NJH&leftTicketDTO.to_station=WHN&purpose_codes=ADULT" % date
    #url can be changed, such as queryA->queryZ... to be completed
    #url = "https://kyfw.12306.cn/otn/leftTicket/queryZ?leftTicketDTO.train_date=%s\
    #&leftTicketDTO.from_station=SHH&leftTicketDTO.to_station=HGH&purpose_codes=ADULT" % date #上海到杭州有票，可以测试一下
    tickets = set([])
    try:
        req = urllib2.urlopen(url, timeout=5)
        data = json.loads(req.read())
        if data['status']:
            trainList = data['data']
            for item in trainList:
                train = item['queryLeftNewDTO']
                tickets.add(train['yz_num'])
                tickets.add(train['rz_num'])
                tickets.add(train['yw_num'])
                tickets.add(train['rw_num'])
                tickets.add(train['gr_num'])
                tickets.add(train['zy_num'])  # 一等座
                tickets.add(train['ze_num'])  # 二等座
                tickets.add(train['tz_num'])
                tickets.add(train['gg_num'])
                tickets.add(train['yb_num'])
                tickets.add(train['wz_num'])  # 无座 PS:不想要此票信息计入就注释掉
                tickets.add(train['qt_num'])
                tickets.add(train['swz_num'])
    except BaseException:
        pass
    return tickets


def filter_tickets_info(tickets):
    if u'--' in tickets:
        tickets.remove(u'--')
    if u'无' in tickets:
        tickets.remove(u'无')
    return tickets


# x, 邮箱账号, such as 'justftt@126.com'
# y, 邮箱密码
def send_mail(sender, password, destination, dates):
    SMTPserver = 'smtp.126.com'
    ticket_url = "https://kyfw.12306.cn/otn/leftTicket/init"
    message = "Click to order tickets: %s\n%s" % (ticket_url, dates)
    msg = MIMEText(message)
    msg['Subject'] = 'Get new ticket info!'
    msg['From'] = sender
    msg['To'] = destination
    mailserver = smtplib.SMTP(SMTPserver, 25)
    mailserver.login(sender, password)
    mailserver.sendmail(sender, destination, msg.as_string())
    mailserver.quit()
    #print 'send email success'


def query():
    args = sys.argv
    while True:
        print "send request"
        valid_tickets = []
        for arg in args[1:]:
            tickets_info = filter_tickets_info(get_tickets(arg))
            print tickets_info
            if u'有' in tickets_info:
                valid_tickets.append({arg, len(tickets_info)})
                send_mail('test@126.com', 'password', "test2@126.com", valid_tickets)
            time.sleep(2)
        time.sleep(10)



query()

# call this function format outside:
# python getTickets.py 2017-01-24 (2017-01-25 (2017-01-26( ...)))
