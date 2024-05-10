import { startCapturingInput, stopCapturingInput } from './input';
import { downloadAssets } from './assets';
import { initState } from './state';
import './css/bootstrap-reboot.css';
import './css/index.css';
import pixiApp from './pixi/app.js';


const playButton = document.getElementById('play-button');
// const usernameInput = document.getElementById('username-input');
// const replayButton = document.getElementById('replay-button');

let flag = true;

Promise.all([

  downloadAssets(),
  

]).then(() => {
  pixiApp();
  console.log('제발떠라');
  // usernameInput.focus();

  playButton.onclick = () => {
    console.log('clickcclick');
    initState();
    startCapturingInput();
    // sessionStorage.setItem('username', usernameInput.value);
    // console.log(sessionStorage.getItem('username'));
    // console.log("hi");
    window.location.href = '/lobby';
    // 로그인 기능 여기다가 넣어야 함
    // 아이디랑 비빌번호를 기억해서 저장해 둬야 함

  };

}).catch(console.error);

// document.getElementById('play-button').addEventListener('click', function () {
//   window.location.href = '/lobby.html';
// });

// /* -------------------------------------------------------- */

// document.getElementById('gotoLobby').addEventListener('click', function () {
//   window.location.href = '/lobby.html';
// });

// document.getElementById('gotoIngame').addEventListener('click', function () {
//   window.location.href = '/ingame.html';
// });

// document.getElementById('gotoIndex').addEventListener('click', function () {
//   window.location.href = '/index.html';
// });