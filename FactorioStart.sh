#!/bin/bash
cd /tmp/
curl -L -o /tmp/factorio.tar.xz https://factorio.com/get-download/stable/headless/linux64
curl -L -o server-settings.json https://raw.githubusercontent.com/rpg2014/Factorio-server-config/main/server-settings.json

#get save file from s3
mkdir -p /home/factorio/factorio/saves
cp /tmp/server-settings.json /home/factorio/factorio/server-settings.json
cd /home/factorio
curl -o /home/factorio/factorio/saves/savegame 'https://factoriosavegame.s3.us-east-2.amazonaws.com/savegame.zip'

tar -xJf /tmp/factorio.tar.xz

/home/factorio/factorio/bin/x64/factorio --server-settings '/home/factorio/factorio/server-settings.json' --start-server '/home/factorio/factorio/saves/savegame' &
