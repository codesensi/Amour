SET
REFERENTIAL_INTEGRITY FALSE;


-- ----------------------------
-- Records of sys_user
-- ----------------------------
-- 幂等填充：仅当该 id 不存在时才插入，已存在则跳过（不重复、不覆盖）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `id_card`, `email`, `phone`, `avatar`, `remark`)
SELECT t.id,
       t.username,
       t.password,
       t.nickname,
       t.id_card,
       t.email,
       t.phone,
       t.avatar,
       t.remark
FROM (VALUES (1, 'boy', '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism', '男孩儿',
              '110101200001010001',
              'boy@amour.com', '18900000000', 'https://api.dicebear.com/7.x/bottts/svg?seed=sadmin', '男孩儿'),
             (2, 'girl', '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism', '女孩儿',
              '110101200001010002',
              'girl@amour.com', '13800000000', 'https://api.dicebear.com/7.x/bottts/svg?seed=suser',
              '女孩儿')) AS t(id, username, password, nickname, id_card, email, phone, avatar, remark)
WHERE NOT EXISTS (SELECT 1 FROM `sys_user` WHERE `sys_user`.id = t.id);


SET
REFERENTIAL_INTEGRITY TRUE;
