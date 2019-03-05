[license]: https://img.shields.io/badge/License-MIT-brightgreen.svg
[ ![license][] ](./LICENSE)
![Build Status](https://travis-ci.org/Chromecube/abbreviation.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b2fdcfd8291242f4b479794eff6c6b01)](https://app.codacy.com/app/chromecube/abbreviation?utm_source=github.com&utm_medium=referral&utm_content=Chromecube/abbreviation&utm_campaign=Badge_Grade_Settings)

![logo](./res/logo128x128.png "Logo")
# Abbreviation

Abbreviation helps you at calling Java shortcut snippets using
a gamepad for input. You write your Java code in an editor and
execute it by typing the specified gamepad input.

## Table of Contents

- [How to Use](#how-to-use)
- [Information about Platform Compatibility](#cross-platform-compatibility)
- [Libraries Used](#used-libraries-and-sources)
- [Build Instructions](#building-it-yourself)

## How to Use

### Creating an abbreviation

1. Simply press the input you want to create an abbreviation for.
You can also use the sticks as well as the triggers.
2. If you are done typing (you can always see your combination in
the lower right corner), press the `BACK` or `SELECT` button.
3. Your default text editor appears. Proceed reading at
[Editing an abbreviation](#editing-an-abbreviation)

### Editing an abbreviation

1. If you have not done this yet, type the combination you want
 to edit and press the `BACK` or `SELECT` key on your gamepad.
2. Now you have your file opened, it is split up into two parts:
3. The <b>init</b> part: Called when the combination is 
initialized.
4. The <b>run</b> part: Called when the combination is being
executed.
5. When you are done editing, simply save and close the editor.
6. <b>Important:</b> Never forget to [<b>reload</b> the
combinations](#reloading)!

### Running an abbreviation

1. Type the combination you want to run.
2. Press the `START` button.
3. Always make sure to have a `:run` part included!

### Deleting an abbreviation

1. Open the combination in an editor as described in
[Editing an abbreviation](#editing-an-abbreviation).
2. Delete all text so the file has nothing inside.
2. When you `reload`, the combination is gone.

### Reloading

Press `BACK`/`SELECT`+`DPAD_LEFT` (and then the `START` button)
to reload the application

### Closing the application

You can always close it using `BACK`/`SELECT`+`DPAD_UP`.

Alternatively you can also close it using the `tray icon`.


## Cross-Platform Compatibility

The application is developed and tested on a Windows 10 machine,
automated build tests are run by [Travis CI](https://travis-ci.org)
on Linux.

<b>Important: Some features are only available on Windows! (e.g.
Utils.isProcessRunning()! It is recommended to use
the software on a Windows machine.</b>

## Used Libraries and Sources

- [Jamepad](https://github.com/williamahartman/Jamepad) for
receiving Gamepad input
- [Beanshell](https://github.com/beanshell/beanshell) for
live-interpreting code
- [Logback](https://github.com/qos-ch/logback) for logging
- [JUnit](https://github.com/junit-team/junit4) for testing 

Licenses: [Link](./LICENSES.md)

## Building it yourself

To build the software, simply use the following command:

```
gradlew.bat build
```

If you are on a non-Windows system, use:

```
gradlew build
```

The built software is located under 
`build/libs/abbreviation-<VERSION>.jar`.
 
You can also download the software from the releases
page.
