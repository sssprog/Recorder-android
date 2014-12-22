#!/bin/bash

export ORG_GRADLE_PROJECT_storeFile="/Users/sergu/projects/Android/sssprog_keystore"
export ORG_GRADLE_PROJECT_keyAlias="sssprog"
 
read -s -p "Keystore Password: " STORE_PASS
echo
read -s -p "Key Password: " KEY_PASS
echo
 
export ORG_GRADLE_PROJECT_storePassword="$STORE_PASS"
export ORG_GRADLE_PROJECT_keyPassword="$KEY_PASS"

./gradlew clean app:assembleRelease
