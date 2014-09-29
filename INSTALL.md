INSTALL
=======

Prerequisites
-------------

    sudo apt-get install openjdk-7-jdk ant libav-tools inkscape

Run tests
---------

    make test

IntelliJ IDEA
-------------

The main directory contains IntelliJ project files - the project should
compile and run out of the box - please contact us if not.

You should have 2 separate IntelliJ projects - one for non-Android and
(optional) one for Android (see below).  Avoid loading the directory
rabbit-escape-ui-android into the non-Android project.

Eclipse
-------

There are Eclipse project files, so "File" -> "Import" ->
"Existing projects into workspace" should work.  Avoid the
rabbit-escape-ui-android directory.

You will need to run make before you start.

Android
-------

Before you start, run:

make android-pre

Load the rabbit-escape-ui-android directory as a project into Android
Studio.  From here you should be able to run it as normal.

If you make changes in the non-Android code, run "make android-pre"
again and choose "Synchronize" on the jar file in Android Studio at
app/src/libs/rabbit-escape-generic.jar

Contributing
------------

As far as is practical, all code should be unit tested.

The only directory that can contain java.awt or javax.swing code is
rabbit-escape-ui-swing.  Everything else must not use these packages
since they are not available on Android.


