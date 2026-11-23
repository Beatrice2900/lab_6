LABORATOR 6 - Retea de socializare (MAP)
Autor: Beatrice

1. DESCRIERE
Aplicatie console in Java (Gradle) pentru o retea de socializare. Lab 6 adauga,
peste lab 5, paginare pentru lista utilizatorilor si pentru lista prietenilor
unui utilizator. Paginarea functioneaza doar cu repository.type=DATABASE.

2. CONFIGURARE
Fisierul src/main/resources/config.properties nu este in Git (contine parola).
Copiati config.example.properties in config.properties si completati valorile:
  repository.type=DATABASE   (DATABASE / FILE / MEMORY)
  db.url, db.user, db.password
  ui.page.size=5             (numarul de elemente pe pagina, trebuie sa fie > 0)

3. UTILIZARE
Optiuni noi in meniu:
  11 - Show users on pages
  12 - Show friends of a user on pages (se cere id-ul utilizatorului)
In navigare: [n] pagina urmatoare, [p] pagina anterioara, [0] inapoi la meniu.
Se afiseaza "Pagina x din y". Daca nu exista elemente, pagina 1 din 1 este goala.

4. STRUCTURA (partea noua)
util.paging.Pageable - numarul paginii (de la 0) si dimensiunea paginii
util.paging.Page<E> - elementele de pe pagina si numarul total de elemente
repository.PagingRepository<ID,E> - extinde Repository, metoda findAllOnPage(Pageable)
repository.FriendsPagingRepository - metoda findFriendsOnPage(Long userId, Pageable)
repository.UserDBRepository - implementeaza PagingRepository
repository.FriendshipDBRepository - implementeaza FriendsPagingRepository
service.UserService - getUsersPage, getFriendsPage
ui.ConsoleUi - meniu, navigare intre pagini, calculul numarului de pagini
repository.RepositoryFactory - getPageSize() citeste ui.page.size

5. PAGINARE IN SQL
Utilizatori:
  SELECT id, first_name, last_name FROM users ORDER BY id LIMIT ? OFFSET ?
  SELECT COUNT(*) FROM users
  (ambele interogari pe aceeasi conexiune; OFFSET = pageNumber * pageSize)
Prieteni (o singura interogare cu JOIN, paginata in SQL, nu in Java):
  SELECT u.id, u.first_name, u.last_name FROM users u
  JOIN friendships f ON (u.id = f.id1 AND f.id2 = ?) OR (u.id = f.id2 AND f.id1 = ?)
  ORDER BY u.id LIMIT ? OFFSET ?
  Numarul total: SELECT COUNT(*) FROM friendships WHERE id1 = ? OR id2 = ?
Numarul de pagini = ceil(total / pageSize), calculat in UI.

6. VALIDARI SI ERORI
- pageNumber < 0 sau pageSize <= 0 => IllegalArgumentException (in service).
- O pagina dupa ultima intoarce o pagina goala (fara eroare).
- Utilizator inexistent la prieteni => EntityNotFoundException.
- Cu FILE sau MEMORY, serviciul arunca UnsupportedOperationException cu mesajul
  "Pagination is available only with repository.type=DATABASE", afisat in UI.
- ui.page.size lipsa, nenumeric sau <= 0 => aplicatia nu porneste, cu mesaj clar.
- Interfata nu contine SQL. Interogarile exista doar in repository-urile DB.

7. DECIZII DE DESIGN
- FriendsPagingRepository este o interfata separata, ca serviciul sa depinda de
  interfete, nu de clase concrete (doar RepositoryFactory cunoaste clasele DB).
- Serviciul verifica cu instanceof daca repository-ul suporta paginarea.
- Page foloseste Iterable<E>, nu o lista concreta.

8. TESTARE (demo)
Cu 12 utilizatori si ui.page.size=5: 3 pagini (5+5+2). Dupa adaugare/stergere de
utilizatori numarul de pagini se actualizeaza. Un utilizator fara prieteni afiseaza
"No users to show." si "Pagina 1 din 1". Prietenia merge in ambele sensuri
(id1 si id2). Documentatia Javadoc (engleza) este in folderul docs.