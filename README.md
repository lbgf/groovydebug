# groovydebug

#### 介绍
一种利用java修改groovy语法树的调试方法，就是代码断点和变量获取，
由于没有界面显示，所以做了个线程模拟，运行GroovyTest后，有个线程
OpenKey会3秒钟开锁一次，跳过断点进入下一句代码

我只用到3.x版本，4.x后的没试过，官方好象说是少了一个解释器，只要语法
树没改应该是没问题，有问题留言

