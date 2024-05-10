// Learn more about this file at:
// https://victorzhou.com/blog/build-an-io-game-part-1/#4-client-networking
// import io from 'socket.io-client';
//import { throttle } from 'throttle-debounce';
import { processGameUpdate } from './state.js';
// import constants from '../shared/constants';
// import renderCheckbox from './htmlComponent/checkbox';
// import redis from 'redis';
import { handleChat } from './chat.js';
import { displayRanking } from './codeSpace.js';

const compileSidebar = document.getElementById("compile-sidebar");

// websocket connection
const battleId = 1;
const devaddr = 'localhost';
// const prodaddr = '3.35.214.100';
const addr = process.env.ADDRR;
const saddr = process.env.SADDRR;

const websocket = new WebSocket(`ws://${saddr}:8080/battle/${battleId}`);

const wsconnectedPromise = new Promise(resolve => {
  // to websocket, 이벤트 핸들러 변경
  // io 와는 다르게 WebSocket 을 사용할 때는 이벤트 핸들러를 직접 등록해야 한다.
  websocket.onopen = (() => {
    console.log('Connected to web socket game server!');
    resolve();
  });
});

export let robotId;
export let robotname;
export let username;

// connect 이후 콜백 등록
export const connect = onGameOver => (
  wsconnectedPromise.then(() => {
    // Register callbacks
    // socket.on(Constants.MSG_TYPES.GAME_UPDATE, processGameUpdate);
    // 이벤트 핸들러 등록 (open, close, error 제외한 일반적인 메시지에 대한 이벤트 핸들러)
    // 
    websocket.onmessage = event => {
      const message = JSON.parse(event.data);

      if (message.type === 'senterbattle') {
        robotId = message.robotId;
        robotname = message.robotName;
        username = message.username;
        console.log(`enter game!!`, message);

      } else if (message.type === 'supdate') { // update (움직임 패킷)
        // 플레이어 update
        const update = {
          t: message.update.t,
          robots: message.update.robots,
          bullets: message.update.bullets,
          scans: message.update.scans
        };
        // console.log(update);
        processGameUpdate(update);

      } else if (message.type === 'schat') {
        console.log('schat');
        handleChat(message.robotId, message.content);
      } else if (message.type === 'sdie') {
        console.log('sdie');
      }
    };

    websocket.onclose = () => {
      console.log('Disconnected from server.');
      document.getElementById('disconnect-modal').classList.remove('hidden');
      document.getElementById('reconnect-button').onclick = () => {
        window.location.reload();
      };
    };
  })
);

// send data << 어차피 이거 아님
export const play = (specIndex) => {
  const message = {
    type: 'centerbattle',
    protocol: 'C_EnterBattle',
    specIndex: specIndex,
  };
  websocket.send(JSON.stringify(message));
};

export const submitNewCode = (code, specIndex) => {
  const message = {
    type: 'csubmit',
    protocol: C_Submit,
    username: '',
    robotId: '',
    code: code,
    specIndex: specIndex
  }
  websocket.send(JSON.stringify(message));
}


export const requestLeaderBoard = (battleId) => {
  const url = `http://${saddr}:8080/get/leaderboard?battleId=` + battleId;
  return fetch(url, {
    method: 'GET',
  })
    .then(response => response.json())
    .then(data => {
      console.log('data?');
      console.log(data);
      return data;
    });
};

export const requestTodayRanking = () => {
  const url = `http://${saddr}:8080/get/today_ranking`;
  fetch(url, {
    method: 'GET',
  })
    .then(response => response.json())
    .then(data => {
      console.log(data);
      displayRanking(data);
    });
};

function change_code(index, content, lang) {
  compileSidebar.classList.add("open");
  const url = `http://${saddr}:8080/compile/ingame-robot`;
  let Data = { robotId: robotId, specIndex: index, code: content, lang: lang }
  console.log(Data)
  const params = new URLSearchParams(Data).toString();
  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: params
  })
    .then(response => response.json())
    .then(data => {
      const result = data.exitCode;
      const status = data.content;
      const code = data.code;
      const lang = data.lang
      console.log(data);
      console.log("result: ", result, "status: ", status);
      code_check(result, status, index, code, lang);
      compileSidebar.classList.remove("open");
    })
    .catch(error => {
      console.error('Error:', error);
      compileSidebar.classList.remove("open");
    });
};

window.change_code = change_code;

export function submitChat(content) {
  const message = {
    type: 'cchat',
    protocol: 'C_Chat',
    content: content,
  }
  websocket.send(JSON.stringify(message));
}
