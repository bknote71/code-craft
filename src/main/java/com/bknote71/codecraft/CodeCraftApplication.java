package com.bknote71.codecraft;

import com.bknote71.codecraft.engine.core.battle.Battle;
import com.bknote71.codecraft.engine.core.battle.BattleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeCraftApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeCraftApplication.class, args);

		BattleManager.Instance.startNewBattle();
	}
}
