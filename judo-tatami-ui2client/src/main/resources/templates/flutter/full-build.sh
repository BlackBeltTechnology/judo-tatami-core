#!/bin/bash

./flutterw clean
./flutterw pub get
./flutterw pub run build_runner build --delete-conflicting-outputs
./flutterw run -d chrome 
 