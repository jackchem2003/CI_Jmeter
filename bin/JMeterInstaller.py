import os, sys, hashlib, urllib2, tempfile, zipfile, distutils.core


class JMeterInstaller(object):

    def __init__(self):
        self.jmeter_version = "2.10"
        self.jmeter_dir = "apache-jmeter-%s/" % self.jmeter_version
        self.download_dir = tempfile.mkdtemp()
        self.md5map = {"jmeter.zip": "c021a874fc08e08d6e017e342d3db2b6",
                       "jmp-standard.zip": "bee82c91e06d9eee81bf61618e48066e",
                       "jmp-extras.zip": "f908b5699a9f30e0745740ca11db4ef7",
                       "jmp-extraslibs.zip": "ec2e43400f13de1b2e68d05e7721ac4a"}

    def clean(self):
        print("Removing %s" % self.download_dir)
        distutils.dir_util.remove_tree(self.download_dir)

    def get_file(self, url, local_path):
        print("Downloading " + url)
        stream = urllib2.urlopen(url)
        with(open(self.download_dir + local_path, "wb")) as f:
            f.write(stream.read())

        with(open(f.name, "rb")) as written:
            md5 = hashlib.md5(written.read()).hexdigest()
            if self.md5map[local_path] != md5:
                self.clean()
                raise Exception("MD5 mismatch. Expected %s but received %s" % (self.md5map[local_path], md5))



    def unzip_plugin(self, zip_file, to_dir):
        out = self.jmeter_dir + to_dir
        with(zipfile.ZipFile(self.download_dir + zip_file, "r")) as z:
            z.extractall(out)
            distutils.dir_util.copy_tree(out + "/lib", self.jmeter_dir + "/lib")
            distutils.dir_util.remove_tree(out + "/lib")
        print("JMeter Plugin copied to JMeter lib directory. README for the plugin available at %s%s" % (self.jmeter_dir, to_dir))


    def install_jmeter(self):
        if not os.path.exists(self.jmeter_dir):
            print("Download JMeter")

            jmeter_file = "http://apache.mirrors.tds.net/jmeter/binaries/apache-jmeter-%s.zip" % self.jmeter_version
            self.get_file(jmeter_file, "jmeter.zip")

            with(zipfile.ZipFile(self.download_dir + "jmeter.zip", "r")) as z:
                z.extractall()

            os.chmod(self.jmeter_dir + "/bin/jmeter.sh", 0755)
        else:
            print("JMeter directory [%s] exists... skipping" % self.jmeter_dir)

    def install_plugins(self):
        if not os.path.exists(self.jmeter_dir + "lib/ext/JMeterPlugins-Standard.jar"):
            print("Installing JMeter Plugins")

            self.get_file("http://jmeter-plugins.org/downloads/file/JMeterPlugins-Standard-1.1.2.zip", "jmp-standard.zip")
            self.get_file("http://jmeter-plugins.org/downloads/file/JMeterPlugins-Extras-1.1.2.zip", "jmp-extras.zip")
            self.get_file("http://jmeter-plugins.org/downloads/file/JMeterPlugins-ExtrasLibs-1.1.2.zip", "jmp-extraslibs.zip")

            self.unzip_plugin("jmp-standard.zip", "jmp-standard")
            self.unzip_plugin("jmp-extras.zip", "jmp-extras")
            self.unzip_plugin("jmp-extraslibs.zip", "jmp-extraslibs")
        else:
            print("JMeter plugins appear to exist in %slib/ext" % self.jmeter_dir)

    def install(self):
        try:
            self.install_jmeter()
            self.install_plugins()
            self.clean()
            return os.path.exists(self.jmeter_dir)
        except:
            self.clean()
            print "Unexpected error:", sys.exc_info()[0]
            raise


if __name__ == '__main__':
    jmi = JMeterInstaller()
    res = jmi.install()
    print("Does jmeter install path %s exist? %s" % (jmi.jmeter_dir, res))