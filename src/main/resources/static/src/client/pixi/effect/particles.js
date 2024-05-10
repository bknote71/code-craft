import { getAsset } from '../../assets';

export class BoosterEffect {
  constructor(app , x , y){
    this.app = app;
    this.container = new PIXI.particles.ParticleContainer(100, {
      scale: true,
      position: true,
      rotation: false,
      uvs: false,
      tint: true
    });
    this.minDelta = 0.01;
    this.pointer = new PIXI.Point(x, y);
    this.emitterPos = this.pointer.clone();
    this.emitter = this.createEmitter();
    this.resized = false;

    this.app.stage.addChild(this.container);
    this.app.stage.interactive = true;
    this.app.ticker.add(this.onTick.bind(this));
  }

  removeEffect(){
    this.app.ticker.remove(this.onTick.bind(this))
    this.app.stage.removeChild(this.container);
    delete this.emitter;
  }

  moveTo(x, y) {
    this.pointer.set(x, y);
  }

  createEmitter() {
    const texture = this.createTexture(0, 8, this.app.renderer.resolution);
    const emitter = new PIXI.particles.Emitter(this.container, [texture], {
      autoUpdate: true,
      alpha: {
        start: 0.9,
        end: 0.5
      },
      scale: {
        start: 1,
        end: 0.5,
        minimumScaleMultiplier: 1
      },
      color: {
        start: "#e3f9ff",
        end: "#2196F3"
      },
      speed: {
        start: 0,
        end: 0,
        minimumSpeedMultiplier: 1
      },
      acceleration: {
        x: 0,
        y: 0
      },
      maxSpeed: 0,
      startRotation: {
        min: 0,
        max: 0
      },
      noRotation: true,
      rotationSpeed: {
        min: 0,
        max: 0
      },
      lifetime: {
        min: 0.2,
        max: 0.2
      },  
      blendMode: "normal",
      frequency: 0.01,
      emitterLifetime: -1,
      maxParticles: 100,
      pos: {
        x: 0,
        y: 0
      },
      addAtBack: false,
      spawnType: "point"
    });
    return emitter;
  }

  onTick(delta) {
    if (this.resized) {
      this.app.renderer.resize(window.innerWidth, window.innerHeight);
      this.resized = false;
    }
    if(!this.emitter){
      return;
    }
    if (!this.emitterPos.equals(this.pointer)) {
      const dt = 1;
      const dx = this.pointer.x - this.emitterPos.x;
      const dy = this.pointer.y - this.emitterPos.y;

      if (Math.abs(dx) > this.minDelta) {
        this.emitterPos.x += dx * dt;
      } else {
        this.emitterPos.x = this.pointer.x;
      }

      if (Math.abs(dy) > this.minDelta) {
        this.emitterPos.y += dy * dt;
      } else {
        this.emitterPos.y = this.pointer.y;
      }

      this.emitter.updateOwnerPos(this.emitterPos.x, this.emitterPos.y);
    }
  }

  createTexture(r1, r2, resolution) {
    const c = (r2 + 1) * resolution;
    r1 *= resolution;
    r2 *= resolution;
      
    const canvas = document.createElement("canvas");
    const context = canvas.getContext("2d");
    canvas.width = canvas.height = c * 2;
    
    const gradient = context.createRadialGradient(c, c, r1, c, c, r2);
    gradient.addColorStop(0, "rgba(255,255,255,1)");
    gradient.addColorStop(1, "rgba(255,255,255,0)");
    
    context.fillStyle = gradient;
    context.fillRect(0, 0, canvas.width, canvas.height);
    
    return PIXI.Texture.fromCanvas(canvas);
  }

}

export class explosionEffect{
  constructor(app , x , y){
    this.app = app;
    this.container = new PIXI.particles.ParticleContainer(1000, {
      scale: true,
      position: true,
      rotation: false,
      uvs: false,
      tint: true
    });
    this.emitter = this.createEmitter(x,y);
    this.app.stage.addChild(this.container);
    this.emitter.emit = true;
    
    this.emitter.playOnceAndDestroy(() => {
      this.removeEffect()
    });
  }

  removeEffect(){
    this.app.stage.removeChild(this.container);
    this.emitter = null;
  }

  createEmitter(x,y) {
    const emitter = new PIXI.particles.Emitter(this.container, PIXI.Texture.from(getAsset('particle.png')), {
      alpha: {
          start: 1,
          end: 0.15
      },
      scale: {
          start: 0.1,
          end: 0.3,
          minimumScaleMultiplier: 0.5
      },
      color: {
          start: "#f2ff00",
          end: "#ff0000"
      },
      speed: {
          start: 100,
          end: 200,
          minimumSpeedMultiplier: 1
      },
      acceleration: {
          x: 1,
          y: 1
      },
      maxSpeed: 0,
      startRotation: {
          min: 0,
          max: 360
      },
      noRotation: false,
      rotationSpeed: {
          min: 0,
          max: 0
      },
      lifetime: {
          min: 0.4,
          max: 0.4
      },
      blendMode: "normal",
      frequency: 0.001,
      emitterLifetime: 0.5,
      maxParticles: 1000,
      pos: {
          x: x,
          y: y
      },
      addAtBack: false,
      spawnType: "point"
    });
    return emitter;
  }
 
}

export class warpEffect{
  constructor(app , x , y){
    this.app = app;
    this.container = new PIXI.particles.ParticleContainer(500, {
      scale: true,
      position: true,
      rotation: true,
      uvs: true,
      tint: true
    });
    this.emitter = this.createEmitter(x,y);
    this.app.stage.addChild(this.container);
    this.emitter.emit = true;
    
    this.emitter.playOnceAndDestroy(() => {
      this.removeEffect()
    });
  }

  removeEffect(){
    this.app.stage.removeChild(this.container);
    this.emitter = null;
  }

  createEmitter(x,y) {
    const emitter = new PIXI.particles.Emitter(this.container, PIXI.Texture.from(getAsset('particle.png')), {
      alpha: {
        start: 0.2,
        end: 0.8
      },
      scale: {
        start: 0.1,
        end: 0.2,
        minimumScaleMultiplier: 1
      },
      color: {
        start: "#000000",
        end: "#ffffff"
      },
      speed: {
        start: 100,
        end: 5,
        minimumSpeedMultiplier: 1
      },
      acceleration: {
        x: 1,
        y: 1
      },
      maxSpeed: 0,
      startRotation: {
        min: 0,
        max: 360
      },
      noRotation: false,
      rotationSpeed: {
        min: 3,
        max: 2
      },
      lifetime: {
        min: 0.3,
        max: 0.5
      },
      blendMode: "normal",
      frequency: 0.001,
      emitterLifetime: 1,
      maxParticles: 500,
      pos: {
        x: x,
        y: y
      },
      addAtBack: false,
      spawnType: "burst",
      particlesPerWave: 1,
      particleSpacing: 0,
      angleStart: 0
    });
    return emitter;
  }
 
}

export class warpoutEffect{
  constructor(app , x , y){
    this.app = app;
    this.container = new PIXI.particles.ParticleContainer(500, {
      scale: true,
      position: true,
      rotation: true,
      uvs: true,
      tint: true
    });
    this.emitter = this.createEmitter(x,y);
    this.app.stage.addChild(this.container);
    this.emitter.emit = true;
    
    this.emitter.playOnceAndDestroy(() => {
      this.removeEffect()
    });
  }

  removeEffect(){
    this.app.stage.removeChild(this.container);
    this.emitter = null;
  }

  createEmitter(x,y) {
    const emitter = new PIXI.particles.Emitter(this.container, PIXI.Texture.from(getAsset('particle.png')), {
      alpha: {
        start: 0,
        end: 0.8
      },
      scale: {
        start: 0.1,
        end: 0.1,
        minimumScaleMultiplier: 1
      },
      color: {
        start: "#000000",
        end: "#ffffff"
      },
      speed: {
        start: 100,
        end: 5,
        minimumSpeedMultiplier: 1
      },
      acceleration: {
        x: 1,
        y: 1
      },
      maxSpeed: 0,
      startRotation: {
        min: 0,
        max: 360
      },
      noRotation: false,
      rotationSpeed: {
        min: 3,
        max: 2
      },
      lifetime: {
        min: 0.3,
        max: 0.5
      },
      blendMode: "normal",
      frequency: 0.001,
      emitterLifetime: 1,
      maxParticles: 1000,
      pos: {
        x: x,
        y: y
      },
      addAtBack: false,
      spawnType: "burst",
      particlesPerWave: 1,
      particleSpacing: 0,
      angleStart: 0
    });
    return emitter;
  }
 
}