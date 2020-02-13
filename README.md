# Spotty-android
Android project for spotty

# !!!!!!!OPGELET!!!!!!
Onze app gebruikt de google maps api! Deze bevat restricties, anders kan iedereen deze stelen en geld stelen.
Dit komt omdat de google api gelinkt is aan een facturingsaccount. 

Het kan dus zijn als u via android studio de app draait een authorization failure krijgt. 

Er zijn twee manieren om deze error weg te krijgen.
- Installeer de app via de google play: https://play.google.com/store/apps/details?id=com.spotty.spotty
- Contacteer Tibo Vanheule via:
  - facebook: https://www.facebook.com/tibovanheule
  - mail: tibo.vanheule@ugent.be
  
  Men krijgt de volgende error, wat Tibo nodig heeft is de cert_fingerprint van de debug key store.
  In dit geval dus 9C:AF:65:B5:33:26:40:F2:F8:94:0D:FD:3C:2B:36:AB:E0:7D:E2:C1
  
  > 2019-04-26 09:56:49.221 22749-22813/com.spotty.spotty E/Google Maps Android API: Authorization failure.
  > Please see https://developers.google.com/maps/documentation/android-api/start for how to correctly set up the map.
  > 2019-04-26 09:56:49.227 22749-22813/com.spotty.spotty E/Google Maps Android API: In the Google Developer Console (https://console.developers.google.com)
  >   Ensure that the "Google Maps Android API v2" is enabled.
  >  Ensure that the following Android Key exists:
  >  	API Key: AIzaSyCkG8cGUfY55Qj5wtmq0QAs-c1f_urhGBA
  >  	Android Application (<cert_fingerprint>;<package_name>): 9C:AF:65:B5:33:26:40:F2:F8:94:0D:FD:3C:2B:36:AB:E0:7D:E2:C1;com.spotty.spotty


Indien er nog problemen zijn stuur een mail naar [mail@spotty.tech].

[mail@spotty.tech]: mailto:mail@spotty.tech

# note com.example.package

Bij de feedback rapport is gebleken dat de package com.example.spotty niet volgens de conventie is. Maar de application id in build.gradle is al aangepast naar com.spotty.spotty, sinds dit los staat van de package naam.

Het refactoren van de package naam is vrij moeilijk en geeft rare onverwachte fenomenen.

