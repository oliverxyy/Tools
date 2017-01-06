#!/usr/bin/env python
# coding=utf-8
import time
import os
import math
from imail.imail import IMail


class Review(object):
    def __init__(self, dir):
        self.dir = dir
        self.list = []

    def get_current_date(self):
        return time.strftime('%Y%m%d', time.localtime(time.time()))

    def is_ontime(self, hour):
        n = 0
        while hour > self.get_period(n):
            n += 1
        return self.get_period(n) - hour < 0.5

    def get_period(self, n):
        return 3 * (2 ** n)

    def get_hours_list(self):
        for i in os.listdir(self.dir):
            self.list.append(round((time.time()-time.mktime(time.strptime(i, '%Y%m%d')))/(60*60)))

    def start(self):
        self.get_hours_list()
        print self.list
        for hour in self.list:
            self.is_ontime(hour)


test = Review('/home/oliver/note')
test.start()
IMail('justftt@126.com', 'oliver0').send_mail("18202752058@163.com", "复习", {'plain':'复习到!', 'html':'<html><h1>复习时间到!</h1></html>'}, send_type='mixed', filepath='/home/oliver/Pictures/20140622173838312.png')



