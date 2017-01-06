#!/usr/bin/env python
# coding=utf-8
import time
import os
import math
from imail.imail import IMail


class Review(object):
    def __init__(self, path):
        # path: note directory
        self.path = path
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
        print self.get_period(n) - hour
        return self.get_period(n) - hour < 0.5

    def get_hours_list(self):
        path = self.path
        for i in os.listdir(path):
            print i
            i = i[:i.rfind('.')]
            self.list[i] = round((time.time()-time.mktime(time.strptime(i, '%Y%m%d')))/(60*60))

    def start(self):
        while True:
            self.get_hours_list()
            for filename, hour in self.list.iteritems():
                if self.is_ontime(hour):
                    IMail('justftt@126.com', 'oliver0').send_mail("18202752058@163.com", "开始复习", "", send_type='mixed',
                                                                  filepath=self.path + filename)
            time.sleep(60 * 60)  # check by hours






