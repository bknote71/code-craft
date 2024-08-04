RESOURCES_PATH="./src/main/resources"
TARGET_PORT=8080

# npm install
npm run build

# copy
cp -f  "${RESOURCES_PATH}/static/dist/index.html" "${RESOURCES_PATH}/templates/"
cp -f  "${RESOURCES_PATH}/static/dist/lobby.html" "${RESOURCES_PATH}/templates/"
cp -f  "${RESOURCES_PATH}/static/dist/ingame.html" "${RESOURCES_PATH}/templates/"
cp -f  "${RESOURCES_PATH}/static/dist/signup.html" "${RESOURCES_PATH}/templates/"

## script insert
chmod +x scripts/script-insert.sh
./scripts/script-insert.sh

## execute process

process_id=$(lsof -ti:8080)

if [ -n "$process_id" ]; then
  kill $process_id
fi

./gradlew clean build
java -jar build/libs/*