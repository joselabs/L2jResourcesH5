ALTER TABLE `character_skills_save` ADD COLUMN `effect_total_time` INT NOT NULL default '0' AFTER `effect_cur_time`;
ALTER TABLE `character_summon_skills_save` ADD COLUMN `effect_total_time` INT NOT NULL default '0' AFTER `effect_cur_time`;
ALTER TABLE `character_pet_skills_save` ADD COLUMN `effect_total_time` INT NOT NULL default '0' AFTER `effect_cur_time`;