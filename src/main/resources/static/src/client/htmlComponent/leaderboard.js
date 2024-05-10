import escape from 'lodash/escape.js';
import { throttle } from 'throttle-debounce';
import { requestLeaderBoard, playerId, username } from '../networking.js';

const leaderboard = document.getElementById('leaderboard');
export let challenger = null;
export const updateLeaderboard = throttle(1500, () => {
  var roomId = 1;
  requestLeaderBoard(roomId)
    .then(data => {
      if (!Array.isArray(data) || data.length === 0) {
        return;
      }
      challenger = data[0].username;
      leaderboard.innerHTML = ''; // Clear existing leaderboard content

      // Displaying "Leaderboard" title
      let title = document.createElement('h3');
      title.textContent = "Leaderboard";
      leaderboard.appendChild(title);

      // Create table
      let table = document.createElement('table');
      leaderboard.appendChild(table);

      // Table header
      let thead = document.createElement('thead');
      table.appendChild(thead);

      let headerRow = document.createElement('tr');
      thead.appendChild(headerRow);

      let rankHeader = document.createElement('th');
      rankHeader.textContent = "Rank";
      headerRow.appendChild(rankHeader);

      let usernameHeader = document.createElement('th');
      usernameHeader.textContent = "Name";
      headerRow.appendChild(usernameHeader);

      let scoreHeader = document.createElement('th');
      scoreHeader.textContent = "Score";
      headerRow.appendChild(scoreHeader);

      // Table body
      let tbody = document.createElement('tbody');
      table.appendChild(tbody);

      for (let i = 0; i < Math.min(data.length, 5); i++) {
        let row = document.createElement('tr');

        let rankCell = document.createElement('td');
        rankCell.textContent = i + 1;
        row.appendChild(rankCell);

        let usernameCell = document.createElement('td');
        usernameCell.textContent = escape(data[i].username.slice(0, 15)) || 'Anonymous';
        row.appendChild(usernameCell);

        let scoreCell = document.createElement('td');
        scoreCell.textContent = data[i].score;
        row.appendChild(scoreCell);

        tbody.appendChild(row);
      }

      // If data length is less than 5, fill up remaining rows with '-'
      for (let i = data.length; i < 5; i++) {
        let row = document.createElement('tr');

        let rankCell = document.createElement('td');
        rankCell.textContent = '-';
        row.appendChild(rankCell);

        let usernameCell = document.createElement('td');
        usernameCell.textContent = '-';
        row.appendChild(usernameCell);

        let scoreCell = document.createElement('td');
        scoreCell.textContent = '-';
        row.appendChild(scoreCell);

        tbody.appendChild(row);
      }
    });
});

export function setLeaderboardHidden(hidden) {
  if (hidden) {
    leaderboard.classList.add('hidden');
  } else {
    leaderboard.classList.remove('hidden');
  }
}