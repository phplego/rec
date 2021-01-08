p="ru.phplego.recfree"
sed 's/$pkg/'${p}'/g' MANIFEST_TPL.xml > ./rec_app_1/AndroidManifest.xml

p="ru.phplego.recpro"
sed 's/$pkg/'${p}'/g' MANIFEST_TPL.xml > ./rec_app_2/AndroidManifest.xml

p="ru.readyscript.secretary"
sed 's/$pkg/'${p}'/g' MANIFEST_TPL.xml > ./rec_app_3/AndroidManifest.xml

p="ru.readyscript.secretarypro"
sed 's/$pkg/'${p}'/g' MANIFEST_TPL.xml > ./rec_app_4/AndroidManifest.xml

echo "Done"
sleep .3

