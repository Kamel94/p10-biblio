TRUNCATE TABLE pret CASCADE;
INSERT INTO pret (id, date_pret, date_retour, utilisateur_id, exemplaire_id, statut, prolongation)
VALUES
(1, '2020/03/21', '2020/04/18', 3, 1, 'PRET', 0),
(2, '2020/04/20', '2020/06/15', 2, 3, 'PRET', 1),
(3, '2020/04/20', '2020/05/18', 3, 2, 'RENDU', 0);

TRUNCATE TABLE reservation CASCADE;
INSERT INTO reservation (id, booking, utilisateur_id, exemplaire_id, statut, notification_date)
VALUES
(1, '2020/08/21', 1, 3, 'EN ATTENTE', null),
(2, '2020/08/24', 2, 3, 'MIS A DISPO', null),
(3, '2020/08/28', 3, 1, 'ANNULEE', '2020/08/30'),
(4, '2020/08/28', 2, 2, 'ANNULEE', null);