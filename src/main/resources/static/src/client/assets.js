// 정의된 asset 이름들의 목록
const ASSET_IMAGE_NAMES = [
  'ship.svg',
  'ship2.svg',
  'bullet.svg',
  'icon_sound_off.png',
  'icon_sound_on.png',
  'particle.png',
  'crown.png',
  'firework.png',
  'function.png',
  'help.png',
  'webicon.png',
  'gunhead.png',
];

const ASSET_SOUND_NAMES =[
  'BoomTwice.mp3',
  'InsertCoin.mp3',
  'NLP_InGame_Final_Int.mp3',
  'NLP_Lobby_Final_Loop.mp3',

];
// assets을 저장할 객체
const assets = {};

// 모든 asset을 다운로드하는 프로미스. Promise.all은 주어진 모든 프로미스가 이행되면 이행되는 
// 새로운 프로미스를 반환합니다. 이 경우, 모든 asset이 다운로드되면 이 프로미스가 이행됩니다.
const downloadPromise = Promise.all([ASSET_IMAGE_NAMES.map(downloadImageAsset)]).then(()=>{
  return Promise.all([ASSET_SOUND_NAMES.map(downloadSoundAsset)]);
});

// asset을 다운로드하는 함수. 이 함수는 주어진 자산 이름에 대한 이미지를 생성하고, 
// 이미지가 로드되면 assets 객체에 이미지를 저장하고 프로미스를 이행하는 새로운 프로미스를 반환합니다.
function downloadImageAsset(assetName) {
  return new Promise(resolve => {
    const asset = new Image();
    asset.onload = () => {
      console.log(`Downloaded ${assetName}`);
      assets[assetName] = asset;
      resolve();
    };
    asset.src = `/assets/${assetName}`;
  });
}

function downloadSoundAsset(assetName) {
  return new Promise(resolve => {
    const asset = new Audio();
    asset.oncanplaythrough = () => {
      console.log(`Downloaded ${assetName}`);
      assets[assetName] = asset;
      resolve();
    };
    asset.src = `/assets/${assetName}`;
  });
}

// 모든 자산을 다운로드하는 프로미스를 반환하는 함수
export const downloadAssets = () => downloadPromise;

// 주어진 자산 이름에 대한 자산을 반환하는 함수
export const getAsset = assetName => assets[assetName];
