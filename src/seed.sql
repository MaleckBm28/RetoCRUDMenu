-- =========================
-- MAGIC
-- =========================

INSERT INTO PRODUCT (PRODUCT_ID, NAME, PRICE, GAME_TYPE, STOCK, IMAGE_PATH) VALUES
(1, 'Magic Phyrexia - Display Box', 120.00, 'MAGIC', 10, '/imgmarket/mtg/phyrexian-box.jpg'),
(2, 'Magic Phyrexia - Booster Pack', 4.50, 'MAGIC', 100, '/imgmarket/mtg/phyrexian-pack.jpg'),
(3, 'Black Lotus', 15000.00, 'MAGIC', 1, '/imgmarket/mtg/black_lotus.jpg'),
(4, 'Shivan Dragon', 15.00, 'MAGIC', 20, '/imgmarket/mtg/Shivan_Dragon.jpg'),
(5, 'Serra Angel', 5.00, 'MAGIC', 50, '/imgmarket/mtg/Serra_Angel.jpg'),
(6, 'Lightning Bolt', 2.50, 'MAGIC', 100, '/imgmarket/mtg/Lightning_Bolt.jpg'),
(7, 'Jace Beleren', 45.00, 'MAGIC', 5, '/imgmarket/mtg/Jace_Beleren.jpg');

INSERT INTO BOOSTER_BOX (PRODUCT_ID, PACK_AMOUNT) VALUES (1, 36);
INSERT INTO BOOSTER_PACK (PRODUCT_ID, CARD_AMOUNT) VALUES (2, 15);
INSERT INTO CARD (PRODUCT_ID, RARITY) VALUES
(3, 'Legendary'),
(4, 'Epic'),
(5, 'Rare'),
(6, 'Common'),
(7, 'Special');

-- =========================
-- POKEMON
-- =========================

INSERT INTO PRODUCT (PRODUCT_ID, NAME, PRICE, GAME_TYPE, STOCK, IMAGE_PATH) VALUES
(10, 'Pokemon 151 - Elite Box', 150.00, 'POKEMON', 8, '/imgmarket/poke/Pokemon-EliteBox.jpg'),
(11, 'Pokemon 151 - Booster Pack', 5.99, 'POKEMON', 80, '/imgmarket/poke/Pokemon_Pack.jpg'),
(12, 'Pikachu Illustrator', 5000.00, 'POKEMON', 1, '/imgmarket/poke/Pikachu_Illustrator.jpg'),
(13, 'Charizard Base Set', 300.00, 'POKEMON', 3, '/imgmarket/poke/Charizard_Base.jpg'),
(14, 'Mewtwo GX', 25.00, 'POKEMON', 15, '/imgmarket/poke/Mewtwo_GX.jpg'),
(15, 'Bulbasaur', 12.00, 'POKEMON', 30, '/imgmarket/poke/Bulbasaur.jpg'),
(16, 'Squirtle', 12.00, 'POKEMON', 30, '/imgmarket/poke/Squirtle.jpg');

INSERT INTO BOOSTER_BOX (PRODUCT_ID, PACK_AMOUNT) VALUES (10, 20);
INSERT INTO BOOSTER_PACK (PRODUCT_ID, CARD_AMOUNT) VALUES (11, 10);
INSERT INTO CARD (PRODUCT_ID, RARITY) VALUES
(12, 'Legendary'),
(13, 'Epic'),
(14, 'Rare'),
(15, 'Common'),
(16, 'Common');

-- =========================
-- YUGIOH
-- =========================

INSERT INTO PRODUCT (PRODUCT_ID, NAME, PRICE, GAME_TYPE, STOCK, IMAGE_PATH) VALUES
(20, 'Yu-Gi-Oh! 25th Anniv - Box', 90.00, 'YUGIOH', 15, '/imgmarket/yugi/Yugi_Box.jpg'),
(21, 'Yu-Gi-Oh! Legend - Pack', 3.99, 'YUGIOH', 120, '/imgmarket/yugi/Yugi_Pack.jpg'),
(22, 'Blue-Eyes White Dragon', 50.00, 'YUGIOH', 10, '/imgmarket/yugi/Blue_Dragon.jpg'),
(23, 'Dark Magician', 35.00, 'YUGIOH', 12, '/imgmarket/yugi/Dark_Magician.jpg'),
(24, 'Exodia the Forbidden One', 100.00, 'YUGIOH', 2, '/imgmarket/yugi/Exodia.jpg'),
(25, 'Red-Eyes Black Dragon', 25.00, 'YUGIOH', 20, '/imgmarket/yugi/Black_Dragon.jpg'),
(26, 'Kuriboh', 1.50, 'YUGIOH', 200, '/imgmarket/yugi/Kuriboh.jpg');

INSERT INTO BOOSTER_BOX (PRODUCT_ID, PACK_AMOUNT) VALUES (20, 24);
INSERT INTO BOOSTER_PACK (PRODUCT_ID, CARD_AMOUNT) VALUES (21, 9);
INSERT INTO CARD (PRODUCT_ID, RARITY) VALUES
(22, 'Epic'),
(23, 'Epic'),
(24, 'Legendary'),
(25, 'Rare'),
(26, 'Common');

-- =========================
-- DIGIMON
-- =========================

INSERT INTO PRODUCT (PRODUCT_ID, NAME, PRICE, GAME_TYPE, STOCK, IMAGE_PATH) VALUES
(30, 'Digimon New Evolution - Box', 85.00, 'DIGIMON', 12, '/imgmarket/digi/Digimon_Box.jpg'),
(31, 'Digimon New Evolution - Pack', 3.90, 'DIGIMON', 60, '/imgmarket/digi/Digimon_Pack.jpg'),
(32, 'Agumon Alternate Art', 12.00, 'DIGIMON', 4, '/imgmarket/digi/Agumon.jpg'),
(33, 'Gabumon', 8.00, 'DIGIMON', 10, '/imgmarket/digi/Gabumon.jpg'),
(34, 'Patamon', 5.00, 'DIGIMON', 15, '/imgmarket/digi/Patamon.jpg'),
(35, 'Greymon', 10.00, 'DIGIMON', 8, '/imgmarket/digi/Greymon.jpg'),
(36, 'Angemon', 15.00, 'DIGIMON', 6, '/imgmarket/digi/Angemon.jpg');

INSERT INTO BOOSTER_BOX (PRODUCT_ID, PACK_AMOUNT) VALUES (30, 24);
INSERT INTO BOOSTER_PACK (PRODUCT_ID, CARD_AMOUNT) VALUES (31, 12);
INSERT INTO CARD (PRODUCT_ID, RARITY) VALUES
(32, 'Special'),
(33, 'Common'),
(34, 'Common'),
(35, 'Rare'),
(36, 'Epic');

-- =========================
-- SHADOWVERSE
-- =========================

INSERT INTO PRODUCT (PRODUCT_ID, NAME, PRICE, GAME_TYPE, STOCK, IMAGE_PATH) VALUES
(40, 'SV Evolve: Vanguard Crossover - Box', 80.00, 'SHADOWVERSE', 20, '/imgmarket/sw/SV_VanguardBox.jpg'),
(41, 'SV Evolve: Vanguard Crossover - Pack', 4.50, 'SHADOWVERSE', 150, '/imgmarket/sw/SV_VanguardPack.jpg'),
(42, 'Blaster Blade (Leader)', 85.00, 'SHADOWVERSE', 2, '/imgmarket/sw/Blaster_Blade.jpg'),
(43, 'Dragonic Overlord', 40.00, 'SHADOWVERSE', 5, '/imgmarket/sw/Dragonic_Overlord.jpg'),
(44, 'Knight of Silence, Gallatin', 1.50, 'SHADOWVERSE', 50, '/imgmarket/sw/Gallatin.jpg'),
(45, 'Future Knight, Llew', 1.00, 'SHADOWVERSE', 60, '/imgmarket/sw/Llew.jpg'),
(46, 'Wingal', 2.00, 'SHADOWVERSE', 40, '/imgmarket/sw/Wingal.jpg');

INSERT INTO BOOSTER_BOX (PRODUCT_ID, PACK_AMOUNT) VALUES (40, 16);
INSERT INTO BOOSTER_PACK (PRODUCT_ID, CARD_AMOUNT) VALUES (41, 8);
INSERT INTO CARD (PRODUCT_ID, RARITY) VALUES
(42, 'Special'),
(43, 'Legendary'),
(44, 'Common'),
(45, 'Common'),
(46, 'Common');
