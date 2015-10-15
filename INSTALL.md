INSTALL
=======

You need Linux
--------------

Rabbit Escape is developed on Linux and uses several tools to
build that are much harder to get working on Windows.  If you'd
like to work on getting it to build on other platforms, we will
happily accept patches.  On Mac it shouldn't be too bad, but on
Windows it's likely to be hard work.

In the meantime, if you are on Windows we'd suggest running
Linux inside a virtual machine.  There are some good
instructions here:
http://www.wikihow.com/Install-Ubuntu-on-VirtualBox .  If this is
slow you might have better results using
[Lubuntu](http://lubuntu.net/) instead of the default Ubuntu.

Prerequisites
-------------

You need Java, Git, Make, Sox and Inkscape to be able to build the
images, sounds and code.  On Ubuntu, Debian and similar:

    sudo apt-get install git make openjdk-7-jdk ant sox inkscape

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

You will need to run `make` before you start.

Eclipse
-------

There are Eclipse project files, so "File" -> "Import" ->
"Existing projects into workspace" should work.  Avoid the
rabbit-escape-ui-android directory.

There is a code formatting profile in doc/eclipse-format.xml.  You can
import it in Window -> Preferences -> Java -> Code Style -> Formatter
then click Import...

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

To run the (very slow) UI tests, right-click androidTest/java in
Android Studio, and click Run 'All Tests'.  Do this before making
a release, trying it on an old Android version (2.2) and a modern
one.

To build the debug APK from the command line:

    make android-debug

This will create a file at
rabbit-escape-ui-android/app/build/outputs/apk/app-debug.apk .

Contributing
------------

As far as is practical, all code should be unit tested.

The only directory that can contain java.awt or javax.swing code is
rabbit-escape-ui-swing.  Everything else must not use these packages
since they are not available on Android.

Please try to stick to the code format as you find it - braces on
their own lines, 4 spaces for indentation (not tabs), spaces inside
brackets.

Doxygen
-------

You can view Doxygen documentation for the code at:
http://artificialworlds.net/rabbit-escape/doxygen/

To build the Doxygen documentation, install the prerequisites:

    sudo apt-get install doxygen graphviz

Then build it with:

    make doxygen

