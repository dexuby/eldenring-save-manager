# Elden Ring Save Manager

An easy-to-use save manager for the action role-playing game 'Elden Ring' developed by FromSoftware and published by Bandai
Namco.

## How to use
Download the latest release (or compile the project yourself) then run it from your command line using: ```java -jar
eldenring-save-manager-<version>.jar```

To select a menu option simply type the number from the brackets in-front and confirm it by pressing enter.

## Features

### Save copying:

You can easily copy saves from one save file to another or copy an extracted save into your save file.

### Save extraction:

Allows you to easily extract single saves out of an Elden Ring save file which by default contains all
saves. This allows you to manage individual saves more easily. On top of that extracted saves will get
heavily compressed resulting in much lower storage consumption (check out the `Compression` section for details).

### Full format reading:

While the size and offsets of saves are generally fix in the Elden Ring save file and can usually
be stored safely as constants this project actually reads the format to extract the actual offsets
and sizes making it more future-proof. In the unrealistic case that they suddenly change the format
resulting in different offsets or save sizes a system with constant offsets and sizes would break and in a
worst-case-scenario even corrupt your saves. This will not happen with this system since the updated values
would be used straight away.

### Cross-platform:

Since this project is written in Java and doesn't use Windows specific features or environment variables it can
technically be used on all operating systems that have the JRE installed.

### Easy to use command-line menu:

Everything gets controlled and managed through an easy-to-use command line menu. Simply enter the menu point
you want to use and follow the simple instructions provided in-program.

## The .er file format

The .er file format is being used to stored extracted Elden Ring saves as a singular file.
This makes it easier to name, organize and compress them. No additional data is being added to
said files to keep them as small as possible. Extracted saves will get compressed lossless
utilizing level 8 Zstd.

The first 588 bytes hold the save header data. The next 16 bytes represent the MD5 checksum for
the save data while the last 2 621 440 bytes contain the actual save data. In total a
save occupies 2 622 044 bytes (2,622044 MB) before compression.

## Compression

As mentioned in the `The .er file format` section extracted saves get stored in a single file and
compressed using level 8 Zstd. This reduces the required space per save from 2 622 044 bytes (2,622044 MB)
to around 169 500 bytes (0,1695 MB) on average - a reduction of roughly 15.46 times. Storing a bunch of
Elden Ring saves (for example to test multiple builds or for content creation purposes) can consume
quite a bit of storage especially if you have to store copies of the full save file which includes all saves.