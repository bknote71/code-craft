import { getAsset } from '../../assets';
import { robotId } from '../../networking';
import { explosionEffect ,warpEffect } from '../effect/particles'
import { challenger } from '../../htmlComponent/leaderboard';
const Constants = require('../../../shared/constants');
const { PLAYER_RADIUS,PLAYER_MAXENERGY, PLAYER_MAXHP } = Constants;


export const playerSprites = {};

export function renderPlayer(player, app) {
    
    const { id , dead , username } = player;
    
    // console.log(id);
    if(dead){
        const sprite = playerSprites[id];
        if(sprite){
            // 폭발 이펙트
            new explosionEffect(app,player.x,player.y);
            // 폭발 사운드
            const expSound = new Audio(getAsset('BoomTwice.mp3').src);
            expSound.volume = 0.1; 
            expSound.loop = false;
            expSound.addEventListener('ended', () => {
                expSound.remove();
            })
            expSound.play();

            app.stage.removeChild(sprite);
            delete playerSprites[id];
        }
        return;
    }

    let robot = playerSprites[id];

    if(!robot){

        setTimeout(() => {
            new warpEffect(app,robot.getChildAt(0).x,robot.getChildAt(0).y);
            app.stage.addChild(robot);
            animateScale(robot);
        }, 100 )

        robot = createNewPlayerSprite(player);
        playerSprites[id] = robot;

    }else{
        
        updatePlayerSpriteData(robot,player);

    }


    if (username === challenger){
        if(robot.children.length > 5){
            updateCrown(robot,player);
        }else{
            createNewCrown(robot,player);
        }
    }else{
        if(robot.children.length > 5){
            robot.removeChildAt(5);
        }
    }

}

function createNewCrown(sprite , player){
    const { x, y} = player;
    const canvasX = x;
    const canvasY = y;

    const crown = new PIXI.Sprite(PIXI.Texture.from(getAsset('crown.png')));
    crown.anchor.set(0.5);
    crown.x = canvasX;
    crown.y = canvasY - PLAYER_RADIUS *2 + 5;
    crown.tint = 0xFFD700
    crown.width = 50;
    crown.height = 50;
    sprite.addChildAt(crown,5);
}

function updateCrown(sprite , player){
    const { x, y } = player;
    const canvasX = x;
    const canvasY = y;

    sprite.getChildAt(5).x = canvasX;
    sprite.getChildAt(5).y = canvasY  - PLAYER_RADIUS *2 + 5;

    
}

function animateScale(robot){
    const scaleSpeed = 2;
    if (robot.getChildAt(0).width < PLAYER_RADIUS * 2) {
        robot.getChildAt(0).width = Math.min(PLAYER_RADIUS * 2, robot.getChildAt(0).width + scaleSpeed);
        robot.getChildAt(0).height = Math.min(PLAYER_RADIUS * 2, robot.getChildAt(0).height + scaleSpeed);
        robot.getChildAt(1).width = Math.min(PLAYER_RADIUS  , robot.getChildAt(0).width + scaleSpeed);
        robot.getChildAt(1).height = Math.min(PLAYER_RADIUS + 10  , robot.getChildAt(0).height + scaleSpeed);
        requestAnimationFrame(() => animateScale(robot));
    }
}

function createNewPlayerSprite(player){
    const robotContainer = new PIXI.Container();
    const { id, x, y, username , bodyHeading, gunHeading, raderHeading ,hp } = player;
    
    const canvasX = x;
    const canvasY = y;
    let randcolor = 0xFFFFFF;

    /* console.log("robotId" + robotId);
    console.log("id " + id); */
    if(robotId === id){
        randcolor = 0x00ff00;
    }else{
        randcolor = 0xFFFFFF;
    }
    
    //spaceship${robotId%8}.png
    // 배 그리기
    const bodyship = new PIXI.Sprite(PIXI.Texture.from(getAsset(`ship.svg`)));
    bodyship.anchor.set(0.5);
    bodyship.x = canvasX;
    bodyship.y = canvasY;
    bodyship.rotation = Math.PI - bodyHeading;
    bodyship.width = 10;
    bodyship.height = 10;
    bodyship.tint = randcolor
    robotContainer.addChildAt(bodyship,0);
    
    const gunhead = new PIXI.Sprite(PIXI.Texture.from(getAsset('gunhead.png')));
    gunhead.anchor.set(0.5);
    gunhead.x = canvasX;
    gunhead.y = canvasY;
    gunhead.rotation = Math.PI - gunHeading;
    gunhead.width = 0;
    gunhead.height = 0;
    gunhead.tint = 0x000000;
    robotContainer.addChildAt(gunhead,1);
    
    //체력이 5이상이라도 5 라고 표현
    const simhp = hp > 5 ? 5 : hp;
    const ratiohp = simhp === 0 ? 0 : simhp / 5 * PLAYER_MAXHP;

    //체력바
    const innerBar = new PIXI.Graphics();
    innerBar.beginFill(0xFFFFFF);
    innerBar.drawRect(canvasX - (PLAYER_RADIUS * 2) + 11, canvasY + (PLAYER_RADIUS * 2)+ 10, PLAYER_MAXHP, 10);
    innerBar.endFill();
    robotContainer.addChildAt(innerBar,2);
    const outerBar = new PIXI.Graphics();
    outerBar.beginFill(0xFF3300);
    outerBar.drawRect(canvasX - (PLAYER_RADIUS * 2) + 11 , canvasY + (PLAYER_RADIUS * 2)+ 10,ratiohp , 10);
    outerBar.endFill();
    robotContainer.addChildAt(outerBar,3); 


    /* //체력바 텍스트
    const hptext = new PIXI.Text(hp, { fontFamily: 'Arial', fontSize: 16, fill: 'white' });
    hptext.anchor.set(0.5);
    hptext.x = canvasX;
    hptext.y = canvasY + (PLAYER_RADIUS * 2) + 10;
    robotContainer.addChildAt(hptext,2); */

    randcolor = Math.floor(randcolor);
    const fillValue = '#' + randcolor.toString(16).padStart(6, '0');
    // 텍스트 그리기
    const text = new PIXI.Text(username, { fontFamily: 'Arial', fontSize: 20, fill: fillValue });
    text.anchor.set(0.5);
    text.x = canvasX;
    text.y = canvasY + PLAYER_RADIUS + 20;    
    robotContainer.addChildAt(text,4);

    //robotContainer.scale.set(0); // 초기 스케일을 0으로 설정
    return robotContainer;
}

function updatePlayerSpriteData(sprite,player){
    const { id, x, y , bodyHeading, gunHeading ,hp } = player;

    let randcolor = 0xFFFFFF;
    if(robotId === id){
        randcolor = 0x00ff00;
    }else{
        randcolor = 0xFFFFFF;
    }

    sprite.getChildAt(0).x = x;
    sprite.getChildAt(0).y = y;
    sprite.getChildAt(0).rotation = Math.PI - bodyHeading;
    sprite.getChildAt(0).tint = randcolor;

    sprite.getChildAt(1).x = x;
    sprite.getChildAt(1).y = y;
    sprite.getChildAt(1).rotation = Math.PI - gunHeading;

    /* sprite.getChildAt(2).x = x;
    sprite.getChildAt(2).y = y + (PLAYER_RADIUS * 2) + 10;
    sprite.getChildAt(2).text = hp; */
    
    //체력이 5이상이라도 5 라고 표현
    const simhp = hp > 5 ? 5 : hp;
    const ratiohp = simhp === 0 ? 0 : simhp / 5 * PLAYER_MAXHP;

    sprite.getChildAt(2).clear();
    sprite.getChildAt(2).beginFill(0xFFFFFF);
    sprite.getChildAt(2).drawRect(x - (PLAYER_RADIUS * 2) + 11 , y + (PLAYER_RADIUS * 2)+ 10, PLAYER_MAXHP, 10);
    sprite.getChildAt(2).endFill();

    sprite.getChildAt(3).clear();
    sprite.getChildAt(3).beginFill(0xFF3300);
    sprite.getChildAt(3).drawRect(x - (PLAYER_RADIUS * 2) + 11 , y + (PLAYER_RADIUS * 2)+ 10, ratiohp, 10);
    sprite.getChildAt(3).endFill();

    sprite.getChildAt(4).x = x;
    sprite.getChildAt(4).y = y + PLAYER_RADIUS + 20;
    //sprite.getChildAt(4).text = name;

}
