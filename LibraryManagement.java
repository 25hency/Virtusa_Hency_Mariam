import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class Book {
    private int bookId;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;
    private String category;
    private double price;

    public Book(int bookId, String title, String author, int totalCopies, String category, double price) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.category = category;
        this.price = price;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }

    public void setAvailableCopies(int count) { this.availableCopies = count; }
    public void setTotalCopies(int count) { this.totalCopies = count; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(double price) { this.price = price; }

    public boolean issueBook() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    @Override
    public String toString() {
        return "Book{" +
                "ID=" + bookId + ", Title='" + title + '\'' + ", Author='" + author + '\'' +
                ", Available=" + availableCopies + "/" + totalCopies + '}';
    }
}


class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private LocalDate registrationDate;
    private List<IssuedBook> issuedBooks;

    public User(int userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.registrationDate = LocalDate.now();
        this.issuedBooks = new ArrayList<>();
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public java.util.List<IssuedBook> getIssuedBooks() { return issuedBooks; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    public void issueBook(Book book, int days) {
        if (book.issueBook()) {
            IssuedBook issuedBook = new IssuedBook(book, LocalDate.now(), days);
            issuedBooks.add(issuedBook);
        }
    }

    public boolean returnBook(Book book) {
        for (IssuedBook ib : issuedBooks) {
            if (ib.getBook().getBookId() == book.getBookId() && !ib.isReturned()) {
                ib.setReturned(true);
                ib.setReturnDate(LocalDate.now());
                book.returnBook();
                return true;
            }
        }
        return false;
    }

    public double calculateFine() {
        double totalFine = 0;
        for (IssuedBook ib : issuedBooks) {
            totalFine += ib.calculateFine();
        }
        return totalFine;
    }
}


class IssuedBook {
    private Book book;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private static final double FINE_PER_DAY = 5.0;

    public IssuedBook(Book book, LocalDate issueDate, int days) {
        this.book = book;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(days);
        this.returned = false;
        this.returnDate = null;
    }

    public Book getBook() { return book; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returned; }

    public void setReturned(boolean returned) { this.returned = returned; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isOverdue() {
        if (returned) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        LocalDate checkDate = returned ? returnDate : LocalDate.now();
        if (checkDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, checkDate);
        }
        return 0;
    }

    public double calculateFine() {
        if (isOverdue()) {
            return getDaysOverdue() * FINE_PER_DAY;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "IssuedBook{" +
                "Book='" + book.getTitle() + '\'' +
                ", IssueDate=" + issueDate +
                ", DueDate=" + dueDate +
                ", Returned=" + returned +
                ", Fine=" + calculateFine() +
                '}';
    }
}


class Library {
    private List<Book> books;
    private List<User> users;
    private int nextBookId;
    private int nextUserId;

    public Library() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.nextBookId = 1;
        this.nextUserId = 1;
        initializeSampleData();
    }

    private void initializeSampleData() {
        addBook("The Silent Patient", "Alex Michaelides", 5, "Mystery", 399.99);
        addBook("Atomic Habits", "James Clear", 8, "Self-Help", 599.99);
        addBook("The Great Gatsby", "F. Scott Fitzgerald", 3, "Fiction", 249.99);
        addBook("Sapiens", "Yuval Noah Harari", 6, "Non-Fiction", 649.99);
        addBook("The Midnight Library", "Matt Haig", 4, "Fiction", 425.99);
    }

    public void addBook(String title, String author, int copies, String category, double price) {
        Book book = new Book(nextBookId++, title, author, copies, category, price);
        books.add(book);
    }

    public void addUser(String name, String email, String phone) {
        User user = new User(nextUserId++, name, email, phone);
        users.add(user);
    }

    public Book searchBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                return book;
            }
        }
        return null;
    }

    public Book searchBookByAuthor(String author) {
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                return book;
            }
        }
        return null;
    }

    public Book getBookById(int bookId) {
        for (Book book : books) {
            if (book.getBookId() == bookId) {
                return book;
            }
        }
        return null;
    }

    public User getUserById(int userId) {
        for (User user : users) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    public java.util.List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public java.util.List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean issueBook(int userId, int bookId, int days) {
        User user = getUserById(userId);
        Book book = getBookById(bookId);
        
        if (user != null && book != null && book.getAvailableCopies() > 0) {
            user.issueBook(book, days);
            return true;
        }
        return false;
    }

    public boolean returnBook(int userId, int bookId) {
        User user = getUserById(userId);
        Book book = getBookById(bookId);
        
        if (user != null && book != null) {
            return user.returnBook(book);
        }
        return false;
    }

    public void updateBook(int bookId, String title, String author, double price) {
        Book book = getBookById(bookId);
        if (book != null) {
            book.setTitle(title);
            book.setAuthor(author);
            book.setPrice(price);
        }
    }

    public void removeBook(int bookId) {
        books.removeIf(b -> b.getBookId() == bookId);
    }

    public double getUserFine(int userId) {
        User user = getUserById(userId);
        return user != null ? user.calculateFine() : 0;
    }
}


class LibraryGUI extends JFrame {
    private Library library;
    private JTabbedPane tabbedPane;

    public LibraryGUI() {
        library = new Library();
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Issue/Return", createIssueReturnPanel());
        tabbedPane.addTab("Search", createSearchPanel());
        tabbedPane.addTab("Fines", createFinesPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField copiesField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Copies:"));
        inputPanel.add(copiesField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Author", "Available", "Total", "Category", "Price"}, 0);
        JTable booksTable = new JTable(model);
        refreshBooksTable(model);

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> {
            try {
                String title = titleField.getText();
                String author = authorField.getText();
                int copies = Integer.parseInt(copiesField.getText());
                String category = categoryField.getText();
                double price = Double.parseDouble(priceField.getText());

                if (!title.isEmpty() && !author.isEmpty()) {
                    library.addBook(title, author, copies, category, price);
                    refreshBooksTable(model);
                    titleField.setText("");
                    authorField.setText("");
                    copiesField.setText("");
                    categoryField.setText("");
                    priceField.setText("");
                    JOptionPane.showMessageDialog(null, "Book added successfully");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshBooksTable(model));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Email", "Phone", "Registration Date", "Books Issued"}, 0);
        JTable usersTable = new JTable(model);
        refreshUsersTable(model);

        JButton addButton = new JButton("Add User");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            if (!name.isEmpty() && !email.isEmpty()) {
                library.addUser(name, email, phone);
                refreshUsersTable(model);
                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                JOptionPane.showMessageDialog(null, "User registered successfully");
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshUsersTable(model));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createIssueReturnPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField userIdField = new JTextField();
        JTextField bookIdField = new JTextField();
        JTextField daysField = new JTextField();

        inputPanel.add(new JLabel("User ID:"));
        inputPanel.add(userIdField);
        inputPanel.add(new JLabel("Book ID:"));
        inputPanel.add(bookIdField);
        inputPanel.add(new JLabel("Days (Issue):"));
        inputPanel.add(daysField);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"User ID", "Book Title", "Issue Date", "Due Date", "Return Date", "Fine"}, 0);
        JTable transactionsTable = new JTable(model);
        refreshTransactionsTable(model);

        JButton issueButton = new JButton("Issue Book");
        issueButton.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userIdField.getText());
                int bookId = Integer.parseInt(bookIdField.getText());
                int days = Integer.parseInt(daysField.getText());

                if (library.issueBook(userId, bookId, days)) {
                    refreshTransactionsTable(model);
                    JOptionPane.showMessageDialog(null, "Book issued successfully");
                } else {
                    JOptionPane.showMessageDialog(null, "Cannot issue book");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        });

        JButton returnButton = new JButton("Return Book");
        returnButton.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userIdField.getText());
                int bookId = Integer.parseInt(bookIdField.getText());

                if (library.returnBook(userId, bookId)) {
                    refreshTransactionsTable(model);
                    JOptionPane.showMessageDialog(null, "Book returned successfully");
                } else {
                    JOptionPane.showMessageDialog(null, "Cannot return book");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTransactionsTable(model));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(issueButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(refreshButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        JTextField titleSearchField = new JTextField();
        JTextField authorSearchField = new JTextField();

        searchPanel.add(new JLabel("Search by Title:"));
        searchPanel.add(titleSearchField);
        searchPanel.add(new JLabel("Search by Author:"));
        searchPanel.add(authorSearchField);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Author", "Available", "Category", "Price"}, 0);
        JTable searchTable = new JTable(model);

        JButton searchTitleButton = new JButton("Search Title");
        searchTitleButton.addActionListener(e -> {
            model.setRowCount(0);
            Book book = library.searchBookByTitle(titleSearchField.getText());
            if (book != null) {
                model.addRow(new Object[]{book.getBookId(), book.getTitle(), book.getAuthor(), 
                    book.getAvailableCopies(), book.getCategory(), book.getPrice()});
            } else {
                JOptionPane.showMessageDialog(null, "Book not found");
            }
        });

        JButton searchAuthorButton = new JButton("Search Author");
        searchAuthorButton.addActionListener(e -> {
            model.setRowCount(0);
            Book book = library.searchBookByAuthor(authorSearchField.getText());
            if (book != null) {
                model.addRow(new Object[]{book.getBookId(), book.getTitle(), book.getAuthor(), 
                    book.getAvailableCopies(), book.getCategory(), book.getPrice()});
            } else {
                JOptionPane.showMessageDialog(null, "Book not found");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchTitleButton);
        buttonPanel.add(searchAuthorButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel();
        JTextField userIdField = new JTextField(10);
        JButton checkButton = new JButton("Check Fine");

        inputPanel.add(new JLabel("User ID:"));
        inputPanel.add(userIdField);
        inputPanel.add(checkButton);

        JTextArea detailsArea = new JTextArea(20, 60);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        checkButton.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userIdField.getText());
                User user = library.getUserById(userId);
                if (user != null) {
                    double fine = library.getUserFine(userId);
                    StringBuilder details = new StringBuilder();
                    details.append("User: ").append(user.getName()).append("\n");
                    details.append("Email: ").append(user.getEmail()).append("\n");
                    details.append("Phone: ").append(user.getPhone()).append("\n\n");
                    details.append("=========== ISSUED BOOKS ===========\n");
                    for (IssuedBook ib : user.getIssuedBooks()) {
                        details.append("\nBook: ").append(ib.getBook().getTitle()).append("\n");
                        details.append("Author: ").append(ib.getBook().getAuthor()).append("\n");
                        details.append("Issue Date: ").append(ib.getIssueDate()).append("\n");
                        details.append("Due Date: ").append(ib.getDueDate()).append("\n");
                        details.append("Returned: ").append(ib.isReturned()).append("\n");
                        details.append("Fine: Rs. ").append(String.format("%.2f", ib.calculateFine())).append("\n");
                    }
                    details.append("\n=========== TOTAL FINE ===========\n");
                    details.append("Total Fine: Rs. ").append(String.format("%.2f", fine)).append("\n");
                    detailsArea.setText(details.toString());
                } else {
                    JOptionPane.showMessageDialog(null, "User not found");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid User ID");
            }
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        return panel;
    }

    private void refreshBooksTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Book book : library.getAllBooks()) {
            model.addRow(new Object[]{
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getAvailableCopies(),
                book.getTotalCopies(),
                book.getCategory(),
                String.format("%.2f", book.getPrice())
            });
        }
    }

    private void refreshUsersTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (User user : library.getAllUsers()) {
            model.addRow(new Object[]{
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRegistrationDate(),
                user.getIssuedBooks().size()
            });
        }
    }

    private void refreshTransactionsTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (User user : library.getAllUsers()) {
            for (IssuedBook ib : user.getIssuedBooks()) {
                model.addRow(new Object[]{
                    user.getUserId(),
                    ib.getBook().getTitle(),
                    ib.getIssueDate(),
                    ib.getDueDate(),
                    ib.getReturnDate(),
                    String.format("%.2f", ib.calculateFine())
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI());
    }
}
