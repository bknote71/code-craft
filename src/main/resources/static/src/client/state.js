// Learn more about this file at:
// https://victorzhou.com/blog/build-an-io-game-part-1/#7-client-state
import { updateLeaderboard } from './htmlComponent/leaderboard.js';

const Constants = require('../shared/constants');

const { MAP_SIZE } = Constants;
// The "current" state will always be RENDER_DELAY ms behind server time.
// This makes gameplay smoother and lag less noticeable.
const RENDER_DELAY = 100;

// 게임 업데이트를 처리하기 위한 변수들
const gameUpdates = [];
let gameStart = 0;
let firstServerTimestamp = 0;

// 초기 상태를 설정하는 함수
export function initState() {
  gameStart = 0;
  firstServerTimestamp = 0;
}

// 서버로부터 받은 게임 업데이트를 처리하는 함수
export function processGameUpdate(update) {

  // 첫 업데이트라면, 서버의 타임스탬프를 기록하고 게임 시작 시간을 설정
  if (!firstServerTimestamp) {
    firstServerTimestamp = update.t;
    gameStart = Date.now();
  }

  // 업데이트를 배열에 추가
  gameUpdates.push(update);

  // 리더보드 업데이트
  updateLeaderboard(update.leaderboard);

  // 현재 서버 시간 이전의 첫 번째 게임 업데이트만 유지
  const base = getBaseUpdate();
  if (base > 0) {
    gameUpdates.splice(0, base);
  }
}

// 현재 서버 시간을 계산하는 함수
function currentServerTime() {
  return firstServerTimestamp + (Date.now() - gameStart) - RENDER_DELAY;
}

// 현재 서버 시간 이전의 첫 번째 게임 업데이트의 인덱스를 반환하거나,
// 해당하는 업데이트가 없다면 -1을 반환하는 함수
function getBaseUpdate() {
  const serverTime = currentServerTime();
  for (let i = gameUpdates.length - 1; i >= 0; i--) {
    if (gameUpdates[i].t <= serverTime) {
      return i;
    }
  }
  return -1;
}

// 현재 상태 반환 { me, others, bullets }
export function getCurrentState() {
  if (!firstServerTimestamp) {
    return {};
  }

  const base = getBaseUpdate();
  const serverTime = currentServerTime();

  // base가 가장 최근의 업데이트인 경우, 해당 업데이트의 상태를 사용
  // 그렇지 않은 경우, base 업데이트와 (base + 1) 업데이트의 상태를 보간
  if (base < 0 || base === gameUpdates.length - 1) {
    return gameUpdates[gameUpdates.length - 1];
  } else {
    const baseUpdate = gameUpdates[base];
    const next = gameUpdates[base + 1];
    const ratio = (serverTime - baseUpdate.t) / (next.t - baseUpdate.t);
    return {
      //me: interpolateObject(baseUpdate.me, next.me, ratio),
      robots: interpolateObjectArray(baseUpdate.robots, next.robots, ratio),
      bullets: interpolateObjectArray(baseUpdate.bullets, next.bullets, ratio),
      scans: interpolateObjectArray(baseUpdate.scans, next.scans, ratio),
    };
  }
}

// 두 객체 사이를 보간하는 함수
function interpolateObject(object1, object2, ratio) {
  if (!object2) {
    return object1;
  }

  const interpolated = {};
  Object.keys(object1).forEach(key => {
    if (key === 'direction') {
      interpolated[key] = interpolateDirection(object1[key], object2[key], ratio);
    } else if (key === 'y'||  key === 'robotY') {
      interpolated[key] = interpolateYPosition(object1[key], object2[key], ratio);
      //interpolated[key] = object1[key] + (object2[key] - object1[key]) * ratio;
    } else if (key === 'x' || key === 'robotX') {
      interpolated[key] = interpolateXPosition(object1[key], object2[key], ratio);
    } else if (key === 'angleStart'){
      interpolated[key] = interpolateAngle(object1[key], object2[key], ratio);
    } else if (key === 'angleExtent'){
      interpolated[key] = object1[key] + (object2[key] - object1[key]) * ratio;
    } else {
      interpolated[key] = object2[key];
    }
  }); 
  return interpolated;
}

// 객체 배열을 보간하는 함수
function interpolateObjectArray(objects1, objects2, ratio) {
  return objects1.map(o => interpolateObject(o, objects2.find(o2 => o.id === o2.id), ratio));
}

// 위치 값을 보간하는 함수
function interpolateYPosition(p1, p2, ratio) {
  const distance = Math.abs(p2 - p1);
  if (distance > MAP_SIZE / 2) {
    // 경계를 넘어가는 경우, 반대편으로 회전하여 보간
    if (p1 < p2) {
      p1 += MAP_SIZE;
    } else {
      p1 -= MAP_SIZE;
    }
  }
  return p1 + (p2 - p1) * ratio;
}

function interpolateXPosition(p1, p2, ratio) {
  const distance = Math.abs(p2 - p1);
  if (distance > 1500 / 2) {
    // 경계를 넘어가는 경우, 반대편으로 회전하여 보간
    if (p1 < p2) {
      p1 += 1500;
    } else {
      p1 -= 1500;
    }
  }
  return p1 + (p2 - p1) * ratio;
}

// Determines the best way to rotate (cw or ccw) when interpolating a direction.
// For example, when rotating from -3 radians to +3 radians, we should really rotate from
// -3 radians to +3 - 2pi radians.
// 방향을 보간할 때 회전 방향 (시계 방향 또는 반시계 방향)을 결정하는 함수
function interpolateDirection(d1, d2, ratio) {
  const absD = Math.abs(d2 - d1);
  if (absD >= Math.PI) {
    // 두 방향 사이의 각도가 크면, 다른 방향으로 회전
    if (d1 > d2) {
      return d1 + (d2 + 2 * Math.PI - d1) * ratio;
    } else {
      return d1 - (d2 - 2 * Math.PI - d1) * ratio;
    }
  } else {
    // 일반 보간
    return d1 + (d2 - d1) * ratio;
  }
}

function interpolateAngle(angle1, angle2, ratio) {
  // 각도의 차이를 계산하고 절대값으로 변환
  let diff = angle2 - angle1;

  // 순환성 보정
  if (Math.abs(diff) > 180) {
    if (diff > 0) {
      angle2 -= 360;
    } else {
      angle2 += 360;
    }
    diff = angle2 - angle1;
  }

  // 보간된 각도 계산
  let interpolatedAngle = angle1 + diff * ratio;

  // 0~360도 범위 내로 조정
  interpolatedAngle = (interpolatedAngle + 360) % 360;

  return interpolatedAngle;
}