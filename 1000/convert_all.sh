#!/bin/bash

for f in *.TXT; do X=`echo $f | sed s/"\."/" "/g | awk '{print $1}'` ; ./convert $f 1000000 > ./$X.vrx ; echo $X; done