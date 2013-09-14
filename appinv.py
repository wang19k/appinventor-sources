#!/usr/bin/python
from bottle import run,route,app,request,response,template,default_app,Bottle,debug,abort,static_file
import sys
import os
import os.path
import subprocess
import re
import jsonlib
#from flup.server.fcgi import WSGIServer
#from cStringIO import StringIO
#import memcache

app = Bottle()
default_app.push(app)

DIR = os.path.join(os.path.dirname(__file__), "web") + os.path.sep

@route('/')
def index():
    return static_file('index.html', root=DIR, mimetype='text/html')

@route('/blocklyframe.html')
def gwtcss():
    return static_file('blocklyframe.html', root=DIR)

@route('/gwt.css')
def gwtcss():
    return static_file('gwt.css', root=DIR, mimetype='text/css')

@route('/Ya.css')
def yacss():
    return static_file('Ya.css', root=DIR, mimetype='text/css')

@route('/media/<filename:path>')
def media(filename):
    media_path = DIR + "media/"
    return static_file(filename, root=media_path)

@route('/images/<filename:re:.*\.png>')
def images(filename):
    image_path = DIR + "images/"
    return static_file(filename, root=image_path , mimetype='image/png')

@route('/images/<filename:re:.*\.jpg>')
def images1(filename):
    image_path = DIR + "images/"
    return static_file(filename, root=image_path , mimetype='image/jpeg')

@route('/ode/download/file/<filename:path>')
def download(filename):
    print 'DOWNLOAD: ' + filename
    z = filename.split('/')
    filename = '/'.join(z[1:])
    return static_file(filename, root='project')

@route('/closure-library-20120710-r2029/<filename:path>')
def closure(filename):
    closure_path = DIR + 'closure-library-20120710-r2029'
    return static_file(filename, root=closure_path)

@route('/fonts/<path>')
def fonts(path):
    data = open(DIR + 'fonts/' + path).read()
    return data

@route('/blockly-all.js')
def blockly():
    data = open(DIR + 'blockly-all.js').read()
    return data

@route('/ode/<ifile>')
def ode(ifile=None):
    if not ifile:
        return 'NO'
    if ifile.startswith('download'):
        z = ifile.split('/')
        pid = z[2]
        filename = '/'.join(z[3:])
        data = open('project/' + filename).read()
        return data
    data = open(DIR + 'ode/%s' % ifile).read()
    return data

@route('/ode/<ifile>', method='POST')
def odepost(ifile):
    if ifile == 'getmotd':
        return '//OK[0,[],0,7]'
    return parsegwtinput(request.body.read())

def parsegwtinput(input):
    x = input.split('|')
    if x[0] != '7':
        raise Exception('Invalid GWT Version')
    stringtablelen = x[2]
    stringtable = x[3:int(stringtablelen)+3]
    service = x[5]
    method = x[6]
    args = x[int(stringtablelen)+3:]
    print 'String Table Length = %s String Table = %s' % (stringtablelen, stringtable)
    print 'Service = %s, Method = %s, args = %s' % (service, method,args)
    call = GWTSERVICE.get(service + '-' + method, None)
    if not call:
        raise Exception('Invalid Method')
    return call(stringtable, args)

_base64_table = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/', ]

def gwtConvertInt(ginput):
    acc = 0
    while ginput != '':
        c = ginput[0]
        c = _base64_table.index(c)
        acc = acc << 6
        acc = acc + c
        ginput = ginput[1:]
    return acc

def gwtEncodeInt(zint):
    retval = []
    while zint != 0:
        retval.append(_base64_table[zint & 0x3f])
        zint = zint >> 6
    retval.reverse()
    if len(retval) == 0:
        return 'A'
    return ''.join(retval)

def gwtUserService(stringtable, args):
    response = []
    response.insert(0, 7)
    response.insert(0, 0)
    response.insert(0, ["com.google.appinventor.shared.rpc.user.User/3496049707","test@example.com",1])
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
    return r'''//OK[0,28,27,-1,26,25,-7,24,23,-9,22,21,0,20,-9,19,18,0,17,-9,16,15,0,14,3,3,13,1,3,12,-1,11,10,-3,9,8,0,5,-3,7,6,0,5,2,3,4,2,3,2,'GWQC',1,["com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidProjectNode/3999559536","YoungAndroid","java.util.ArrayList/4159755760","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidAssetsFolder/3524809606","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidAssetNode/3698939010","assets/kitty.png","kitty.png","assets/meow.mp3","meow.mp3","assets","Assets","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidSourceFolderNode/1539202537","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidPackageNode/404162700","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidBlocksNode/1973959899","src/appinventor/ai_test/HelloPurr/Screen1.bky","Screen1.bky","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidFormNode/3242031682","src/appinventor/ai_test/HelloPurr/Screen1.scm","Screen1.scm","com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidYailNode/3020652743","src/appinventor/ai_test/HelloPurr/Screen1.yail","Screen1.yail","src/appinventor/ai_test/HelloPurr","appinventor.ai_test.HelloPurr","src","Sources","1664002","HelloPurr"],0,7]'''

def gwtloadProjectSettings(stringtable, args):
    return '//OK[1,["{\"SimpleSettings\":{\"Icon\":\"\",\"ShowHiddenComponents\":\"False\",\"VersionCode\":\"1\",\"VersionName\":\"1.0\",\"UsesLocation\":\"False\"}}"],0,7]'

def gwtLoadProject(stringtable, args):
    pid = gwtConvertInt(args[-3])
    fileoffset = int(args[-2])
    filename = stringtable[fileoffset-1]
    data = open('project/' + filename).read()
    data = data.replace('"',r'\"')
    data = data.replace('\n', r'\n')
    return '//OK[1,["' + data + '"],0,7]'

def gwtLoadrawProject(stringtable, args):
    def _modulo(x):
        if x > 127:
            return x - 256
        else:
            return x
    pid = gwtConvertInt(args[-3])
    fileoffset = int(args[-2])
    filename = stringtable[fileoffset-1]
    data = open('project/' + filename).read()
    retval = []
    datalen = len(data)
    for i in range(datalen):
        retval.append(ord(data[i]))
    retval.reverse()
    retval = [_modulo(x) for x in retval]
    retval.append(datalen)
    retval.append(1)
    retval.append('["[B/3308590456"]')
    retval.append(0)
    retval.append(7)
    return '//OK' + str(retval)

def gwtStoreSettings(stringtable, args):
    return "//OK[[],0,7]"

def gwtSaveProject(stringtable, args):
    return "//OK['UEYgJK$',[],0,7]"

def gwtstoreProjectSettings(stringtable, args):
    return "//OK[[],0,7]"

GWTSERVICE = { 'com.google.appinventor.shared.rpc.user.UserInfoService-getUserInformation' : gwtUserService,
               'com.google.appinventor.shared.rpc.user.UserInfoService-loadUserSettings' : gwtloadUserSettings,
               'com.google.appinventor.shared.rpc.project.ProjectService-getProjectInfos' : gwtgetProjectInfos,
               'com.google.appinventor.shared.rpc.user.UserInfoService-hasUserFile' : gwthasUserFile,
               'com.google.appinventor.shared.rpc.project.ProjectService-getProjects' : gwtgetProjects,
               'com.google.appinventor.shared.rpc.project.ProjectService-getProject' : gwtgetProject,
               'com.google.appinventor.shared.rpc.project.ProjectService-loadProjectSettings' : gwtloadProjectSettings,
               'com.google.appinventor.shared.rpc.project.ProjectService-storeProjectSettings' : gwtstoreProjectSettings,
               'com.google.appinventor.shared.rpc.project.ProjectService-load' : gwtLoadProject,
               'com.google.appinventor.shared.rpc.project.ProjectService-loadraw' : gwtLoadrawProject,
               'com.google.appinventor.shared.rpc.project.ProjectService-save' : gwtSaveProject,
               'com.google.appinventor.shared.rpc.user.UserInfoService-storeUserSettings' : gwtStoreSettings,}

if __name__ == '__main__':
    debug(True)
    run(host='0.0.0.0', port=8005)
    ##WSGIServer(app).run()
