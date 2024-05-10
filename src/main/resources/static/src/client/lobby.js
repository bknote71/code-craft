import { downloadAssets } from './assets';
import './css/bootstrap-reboot.css';
import pixiApp from './pixi/app.js';
import './css/lobby/lobby.css';
import './css/lobby/start-button.css';
import './css/lobby/module-button.css';
import './css/compile-button.css';
import './css/lobby/event-button.css';
import './css/lobby/neon-button.css';
import { getRobotInfos } from './networking.js';
import { requestTodayRanking } from './networking.js';

// const startButton = document.getElementById('start-button');

let flag = true;

Promise.all([

  downloadAssets(),
  
  
]).then(() => {
  pixiApp();
  document.addEventListener("DOMContentLoaded", function () {
    requestTodayRanking();
  });

}).catch(console.error);

// export function displayRanking(data) {
//   let rankingDiv = document.getElementById('today-rank');
//   // Clear any previous content
//   rankingDiv.innerHTML = '';

//   // Displaying "Today Rank" title
//   let title = document.createElement('h3');
//   title.textContent = "Today Rank";
//   rankingDiv.appendChild(title);

//   // Looping through data and appending to the DOM
//   data.forEach(rankItem => {
//     let item = document.createElement('p');
//     item.textContent = `${rankItem.username}: ${rankItem.score}`;
//     rankingDiv.appendChild(item);
//   });
// }