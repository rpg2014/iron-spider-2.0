#!/bin/bash
cd /tmp/
curl -L -o /tmp/factorio.tar.xz https://factorio.com/get-download/stable/headless/linux64
git clone https://github.com/rpg2014/iron-spider-2.0.git

#get save file from s3
mkdir -p /home/factorio/factorio/saves
cp /tmp/iron-spider-2.0/server-settings.json /home/factorio/factorio/server-settings.json
cd /home/factorio
curl -o /home/factorio/factorio/saves/savegame '  '

tar -xJf /tmp/factorio.tar.xz

/home/factorio/factorio/bin/x64/factorio --server-settings '/home/factorio/factorio/server-settings.json' --start-server '/home/factorio/factorio/saves/savegame' &
