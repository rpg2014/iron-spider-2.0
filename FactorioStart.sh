#!/usr/bin/env bash
cd /tmp/ || exit

curl -L -o /tmp/factorio.tar.xz https://factorio.com/get-download/stable/headless/linux64
#get save file from s3
mkdir -p /home/factorio/factorio/saves
cd /home/factorio || exit

curl -o /home/factorio/factorio/saves/savegame 'presigned s3 url'

tar -xJf /tmp/factorio.tar.xz

/home/factorio/factorio/bin/x64/factorio --start-server 'savegame' &
