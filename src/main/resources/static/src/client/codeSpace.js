export function displayRanking(data) {
  let rankingDiv = document.getElementById('today-rank');
  // Clear any previous content
  rankingDiv.innerHTML = '';

  // Displaying "Today Rank" title
  let title = document.createElement('h3');
  title.textContent = "Today Ranking";
  rankingDiv.appendChild(title);

  // Create table
  let table = document.createElement('table');
  rankingDiv.appendChild(table);

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

  // Looping through data and appending to the table
  data.forEach((rankItem, index) => {
    let row = document.createElement('tr');
    
    let rankCell = document.createElement('td');
    rankCell.textContent = index + 1; // 순위는 1부터 시작
    row.appendChild(rankCell);
    
    let usernameCell = document.createElement('td');
    usernameCell.textContent = rankItem.username;
    row.appendChild(usernameCell);

    let scoreCell = document.createElement('td');
    scoreCell.textContent = rankItem.score;
    row.appendChild(scoreCell);

    tbody.appendChild(row);
  });
}
