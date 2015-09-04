YoutubeMiTunes [![Support via Gratipay](https://cdn.rawgit.com/gratipay/gratipay-badge/2.3.0/dist/gratipay.svg)](https://gratipay.com/~Ale46/)
===================


YoutubeMiTunes is a Java based software that can import your itunes playlist (or also your entire library) on a Youtube private playlist.


Configuration
-------------

Copy the example configuration in your user directory

    mv ymt-keys-example.properties ~/.ymt-keys.properties

 1. Go [HERE](https://console.developers.google.com/project) and create a new project.
 2. Go to credentials
 3. Create a new API KEY and copy\paste the generated API KEY in ~/.ymt-keys.properties
 4. Create a new OAuth 2.0 client ID ,copy the value of CLIENT_ID and CLIENT_SECRET from the generated json in ~/.ymt-keys.properties



Build
-------------
If you don't want the compiled jar from Release section you can compile from source with:

    git clone https://github.com/Ale46/YoutubeMiTunes.git
    cd YoutubeMiTunes
    ant -f youtubemitunes.xml



Usage
-------------

    java -jar youtubemitunes.jar
After the gui shows you need to choose the *.xml* playlist to import on your youtube account. After a while you should see two list with the matched songs (local-youtube),  some matched songs are completely wrong so you need to review every matches and delete manually the errors. Click on Start Import and grant access to YoutubeMiTunes.
Rember: playlist with a lot of songs can have problems.