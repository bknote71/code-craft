// Learn more about this file at:
// https://victorzhou.com/blog/build-an-io-game-part-1/#3-client-entrypoints
import { connect, play, handleChatAttack } from './networking';
import { startCapturingInput, stopCapturingInput } from './input';
import { downloadAssets } from './assets';
import { initState } from './state';
import { setLeaderboardHidden } from './htmlComponent/leaderboard';
// I'm using a tiny subset of Bootstrap here for convenience - there's some wasted CSS,
// but not much. In general, you should be careful using Bootstrap because it makes it
// easy to unnecessarily bloat your site.
import './css/bootstrap-reboot.css';
// import './css/main.css';
import './css/ingame.css';
import './css/compile-button.css';
import pixiApp from './pixi/app.js';
import { submitChat } from './networking.js';
const gamecanvers = document.getElementById('game-canvas');

const playMenu = document.getElementById('play-menu');
const playButton = document.getElementById('play-button');
const usernameInput = document.getElementById('username-input');
const inputbar = document.getElementById('inputbar')
const gameoverMenu = document.getElementById('game-over');
const replayButton = document.getElementById('replay-button');
const usernamereInput = document.getElementById('username-reinput');
const userSelectedLang = document.getElementById('language-select');
let flag = true;

Promise.all([

  downloadAssets(),
  
]).then(() => {
  connect(onGameOver).then(()=>{
    pixiApp().then(()=>{
      play(specIndex);
    });
  });
  
}).catch(console.error);


function playBattle(specIndex) {
    play(specIndex);
}

function onGameOver(obj) {
  gamecanvers.classList.add('hidden');
  stopCapturingInput();
  setLeaderboardHidden(true);
  gameoverMenu.classList.remove('hidden');

  if(flag){// 전판과 똑같은 이름으로 복사
    usernamereInput.value = usernameInput.value;
    flag = false;
  }
  usernamereInput.focus();
  replayButton.onclick = () => {
    gamecanvers.classList.remove('hidden');
    window.removeEventListener('keydown' ,handleEnterKey);
    play(usernamereInput.value);
    gameoverMenu.classList.add('hidden');
    initState();
    startCapturingInput();
    setLeaderboardHidden(false);
  }
}


// 엔터 키 이벤트를 감지하는 함수
function handleEnterKey(event) {
  // event.keyCode는 Enter 키의 키코드가 13입니다.
  if (event.keyCode === 13) {
    // 엔터 키를 누른 경우, playButton을 클릭한 것과 같은 동작을 수행합니다.
    if(flag){
      playButton.click();
    }else{
      replayButton.click();
    }
  }
}

document.getElementById('toggle-main-container').addEventListener('click', function() {
  var mainContainer = document.getElementById('main-container');
  var toggleButton = document.getElementById('toggle-main-container');
  var leaderButton = document.getElementById('toggle-leaderboard');
  var exitButton = document.getElementById('exit-button');
  if(mainContainer.classList.contains('open')) {
    mainContainer.classList.remove('open');
    toggleButton.classList.remove('open');
    leaderButton.classList.remove('open');
    exitButton.classList.remove('open');
  } else {
    mainContainer.classList.add('open');
    toggleButton.classList.add('open');
    leaderButton.classList.add('open');
    exitButton.classList.add('open');
  }
});

document.querySelector('#toggle-event').addEventListener('click', function() {
  var sidebar = document.getElementById('event-sidebar');
  var toggleButton = document.getElementById('toggle-event');
  var moduleButton = document.getElementById('toggle-module');
  var helpButton = document.getElementById('toggle-help');
  var leaderboard = document.getElementById('leaderboard');
  var inputbar = document.getElementById('inputbar');
  var audioBox = document.getElementById('audio-box');
  var leaderButton = document.getElementById('toggle-leaderboard');

  if(sidebar.classList.contains('open')) {

    sidebar.classList.remove('open');
    toggleButton.classList.remove('open');
    leaderboard.classList.remove('hidden-to-left');
    inputbar.classList.remove('hidden-to-left');
    moduleButton.classList.remove('hidden-to-left');
    helpButton.classList.remove('hidden-to-left');
    audioBox.classList.remove('hidden-to-left');
    leaderButton.classList.remove('open');

  } else {

    sidebar.classList.add('open');
    toggleButton.classList.add('open');
    leaderboard.classList.add('hidden-to-left');
    inputbar.classList.add('hidden-to-left');
    moduleButton.classList.add('hidden-to-left');
    helpButton.classList.add('hidden-to-left');
    audioBox.classList.add('hidden-to-left');
    leaderButton.classList.add('open');
  }
});

document.querySelector('#toggle-module').addEventListener('click', function() {
  var sidebar = document.getElementById('module-sidebar');
  var toggleButton = document.getElementById('toggle-module');
  var eventButton = document.getElementById('toggle-event');
  var helpButton = document.getElementById('toggle-help');
  var leaderboard = document.getElementById('leaderboard');
  var inputbar = document.getElementById('inputbar');
  var audioBox = document.getElementById('audio-box');
  var leaderButton = document.getElementById('toggle-leaderboard');

  if(sidebar.classList.contains('open')) {
    sidebar.classList.remove('open');
    toggleButton.classList.remove('open');
    leaderboard.classList.remove('hidden-to-left');
    inputbar.classList.remove('hidden-to-left');
    eventButton.classList.remove('hidden-to-left');
    helpButton.classList.remove('hidden-to-left');
    audioBox.classList.remove('hidden-to-left');
    leaderButton.classList.remove('open');
  } else {
    sidebar.classList.add('open');
    toggleButton.classList.add('open');
    leaderboard.classList.add('hidden-to-left');
    inputbar.classList.add('hidden-to-left');
    eventButton.classList.add('hidden-to-left');
    helpButton.classList.add('hidden-to-left');
    audioBox.classList.add('hidden-to-left');
    leaderButton.classList.add('open');
  }
});

document.querySelector('#toggle-help').addEventListener('click', function() {
  var sidebar = document.getElementById('help-sidebar');
  var toggleButton = document.getElementById('toggle-module');
  var eventButton = document.getElementById('toggle-event');
  var helpButton = document.getElementById('toggle-help');
  var leaderboard = document.getElementById('leaderboard');
  var inputbar = document.getElementById('inputbar');
  var audioBox = document.getElementById('audio-box');
  var leaderButton = document.getElementById('toggle-leaderboard');

  if(sidebar.classList.contains('open')) {
    sidebar.classList.remove('open');
    helpButton.classList.remove('open');
    leaderboard.classList.remove('hidden-to-left');
    inputbar.classList.remove('hidden-to-left');
    eventButton.classList.remove('hidden-to-left');
    toggleButton.classList.remove('hidden-to-left');
    audioBox.classList.remove('hidden-to-left');
    leaderButton.classList.remove('open');
  } else {
    sidebar.classList.add('open');
    helpButton.classList.add('open');
    leaderboard.classList.add('hidden-to-left');
    inputbar.classList.add('hidden-to-left');
    eventButton.classList.add('hidden-to-left');
    toggleButton.classList.add('hidden-to-left');
    audioBox.classList.add('hidden-to-left');
    leaderButton.classList.add('open');
  }
});

inputbar.addEventListener("keyup", function (event) {
  if (event.key === "Enter") {
    // 엔터 키를 눌렀을 때 실행할 동작을 여기에 작성합니다.
    // 예를 들어, 입력한 채팅 메시지를 가져와서 처리하는 등의 작업이 가능합니다.
    var message = event.target.value;
    console.log("입력한 메시지:", message);
    submitChat(message);
    event.target.value = ""; // 입력란 비우기
  }
});

window.onload = function () {
  var audioElement = document.getElementById('audioPlayer');
  audioElement.play();
}

document.querySelector('#toggle-leaderboard').addEventListener('click', function() {
  var leaderButton = document.getElementById('toggle-leaderboard');
  var leaderboard = document.getElementById('leaderboard');

  if(leaderboard.classList.contains('open')) {
    leaderboard.classList.remove('open');
  } else {
    leaderboard.classList.add('open');
  }
});