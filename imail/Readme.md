Introduction:
-----------
#####To use this class, first import the libary:
	from imail.imail import IMail
#####Then, you can use in that way that listed as follows:
	instance = IMail('test1@126.com', 'test')
	instance.send_mail("test2@163.com", "testt", 'testtt!')
#####simple text
	IMail('test1@126.com', 'test').send_mail("test2@163.com", "testt", 'test!')
#####html format
	IMail('test1@126.com', 'test').send_mail("test2@163.com", "testt", '<html><h1>复习时间到!</h1></html>', send_type='html')
#####mixed type, text and html
	IMail('test1@126.com', 'test').send_mail("test2@163.com", "testt", '{"html":"<html>test</html>","plain":"test"}', send_type='mixed')
#####mixed type, text and html attach with file
	IMail('test1@126.com', 'test').send_mail("test2@163.com", "testt", '{"html":"<html>test</html>","plain":"test"}', send_type='mixed', filepath='/home/oliver/Pictures/20140622173838312.png')


