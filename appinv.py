#!/usr/bin/python
from bottle import run,route,app,request,response,template,default_app,Bottle,debug,abort
import sys
import os
import subprocess
import re
import jsonlib
#from flup.server.fcgi import WSGIServer
#from cStringIO import StringIO
#import memcache

app = Bottle()
default_app.push(app)

DIR = '/u1/jis/app-inventor/appinventor/appengine/build/war/'

@route('/')
def index():
    data = open(DIR + 'index.html').read()
    return data

@route('/gwt.css')
def index():
    data = open(DIR + 'gwt.css').read()
    response.set_header('Content-Type', 'text/css')
    return data

@route('/Ya.css')
def index():
    data = open(DIR + 'Ya.css').read()
    response.set_header('Content-Type', 'text/css')
    return data

@route('/images/<path>')
def images(path):
    data = open(DIR + 'images/' + path).read()
    return data

@route('/fonts/<path>')
def fonts(path):
    data = open(DIR + 'fonts/' + path).read()
    return data

@route('/blockly-all.js')
def blockly():
    data = open(DIR + 'blockly-all.js').read()
    return data

@route('/ode/<file>')
def ode(file=None):
    if not file:
        return 'NO'
    data = open(DIR + 'ode/%s' % file).read()
    return data

@route('/ode/<file>', method='POST')
def odepost(file):
    if file == 'getmotd':
        return '//OK[0,[],0,7]'
    return parsegwtinput(request.body.read())

def parsegwtinput(input):
    x = input.split('|')
    if x[0] != '7':
        raise Exception('Invalid GWT Version')
    stringtablelen = x[2]
    stringtable = x[3:int(stringtablelen)]
    service = x[5]
    method = x[6]
    args = x[int(stringtablelen)+3:]
    print 'String Table Length = %s String Table = %s' % (stringtablelen, stringtable)
    print 'Service = %s, Method = %s, args = %s' % (service, method,args)
    call = GWTSERVICE.get(service + '-' + method, None)
    if not call:
        raise Exception('Invalid Method')
    return call(stringtable, args)

def gwtConvertInt(input):
    import base64
    z = base64.decodestring(input)
    

def gwtUserService(stringtable, args):
    response = []
    response.insert(0, 7)
    response.insert(0, 0)
    response.insert(0, ["com.google.appinventor.shared.rpc.user.User/3496049707","Jeffrey.Schiller@gmail.com",1])
    response = [1, 1, 3, 2, 1] + response
    response = '//OK' + str(response)
    return response

def gwtloadUserSettings(stringtable, args):
    return r'//OK[1,["{\"GeneralSettings\":{\"CurrentProjectId\":\"1664002\"},\"SimpleSettings\":{}}"],0,7]'

def gwtgetProjectInfos(stringtable, args):
    return r'''//OK[4,11,'GWQC','UEYgE8G','UC8VOuf',2,4,10,'FGaw','UCjk4Qt','UCjkopt',2,4,9,'BKOB','T95DSYR','T92qa9T',2,4,8,'BGzR','UEUdN9y','T9acTik',2,4,7,'w1B','UBBABWQ','T74pZHF',2,4,6,'b1R','UDlu9uz','T4_NXtv',2,4,5,'Kvh','T_bC1KD','T3NwT9r',2,4,3,'IDp','UCjiKUg','T3DTjoD',2,8,1,["java.util.ArrayList/4159755760","com.google.appinventor.shared.rpc.project.UserProject/2581505632","Pizza_Party","YoungAndroid","foobar","pad","testfoo","test","MITNewBlocksCompanion","initialize_twice","HelloPurr"],0,7]'''

def gwthasUserFile(stringtable, args):
    return '//OK[1,[],0,7]'

def gwtgetProjects(stringtable, args):
    pass

def gwtgetProject(stringtable, args):
    return r'''//OK[0,28,27,-1,26,25,-7,24,23,-9,22,21,0,20,-9,19,18,0,17,-9,16,15,0,14,3,3,13,1,3,12,-1,11,10,-3,9,8,0,5,-3,7,6,0,5,2,3,4,2,3,2,'GWQC',1,["com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidProjectNode/3999559536","YoungAndroid","java.util.ArrayList/4159755760","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidAssetsFolder/3524809606","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidAssetNode/3698939010","assets/kitty.png","kitty.png","assets/meow.mp3","meow.mp3","assets","Assets","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidSourceFolderNode/1539202537","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidPackageNode/404162700","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidBlocksNode/1973959899","src/appinventor/ai_Jeffrey_Schiller/HelloPurr/Screen1.bky","Screen1.bky","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidFormNode/3242031682","src/appinventor/ai_Jeffrey_Schiller/HelloPurr/Screen1.scm","Screen1.scm","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidYailNode/3020652743","src/appinventor/ai_Jeffrey_Schiller/HelloPurr/Screen1.yail","Screen1.yail","src/appinventor/ai_Jeffrey_Schiller/HelloPurr","appinventor.ai_Jeffrey_Schiller.HelloPurr","src","Sources","1664002","HelloPurr"],0,7]'''

def gwtloadProjectSettings(stringtable, args):
    return '//OK[1,["{\"SimpleSettings\":{\"Icon\":\"\",\"ShowHiddenComponents\":\"False\",\"VersionCode\":\"1\",\"VersionName\":\"1.0\",\"UsesLocation\":\"False\"}}"],0,7]'

def gwtLoadProject(stringtable, args):
    pid = args[-2]
    filename = args[-1]
    


GWTSERVICE = { 'com.google.appinventor.shared.rpc.user.UserInfoService-getUserInformation' : gwtUserService,
               'com.google.appinventor.shared.rpc.user.UserInfoService-loadUserSettings' : gwtloadUserSettings,
               'com.google.appinventor.shared.rpc.project.ProjectService-getProjectInfos' : gwtgetProjectInfos,
               'com.google.appinventor.shared.rpc.user.UserInfoService-hasUserFile' : gwthasUserFile,
               'com.google.appinventor.shared.rpc.project.ProjectService-getProjects' : gwtgetProjects,
               'com.google.appinventor.shared.rpc.project.ProjectService-getProject' : gwtgetProject,
               'com.google.appinventor.shared.rpc.project.ProjectService-loadProjectSettings' : gwtloadProjectSettings,
               'com.google.appinventor.shared.rpc.project.ProjectService-load' : gwtLoadProject,}


if __name__ == '__main__':
    debug(True)
    run(host='127.0.0.1', port=8005)
    ##WSGIServer(app).run()
