#!/bin/bash

RESOURCES_PATH="./src/main/resources/templates"

lobbyfile="${RESOURCES_PATH}/lobby.html"
lobby_insert_line="65"
lobby_insert_file="./scripts/lobby-script"

head -n $(($lobby_insert_line - 1)) "$lobbyfile" > tmpfile1
cat "$lobby_insert_file" >> tmpfile1
tail -n +$lobby_insert_line "$lobbyfile" >> tmpfile1

mv tmpfile1 "$lobbyfile"

##########################################################################

ingamefile="${RESOURCES_PATH}/ingame.html"
ingame_insert_line="321"
ingame_insert_file="./scripts/ingame-script"

head -n $(($ingame_insert_line - 1)) "$ingamefile" > tmpfile2
cat "$ingame_insert_file" >> tmpfile2
tail -n +$ingame_insert_line "$ingamefile" >> tmpfile2

mv tmpfile2 "$ingamefile"