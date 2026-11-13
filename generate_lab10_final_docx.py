import os
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement('w:shd')
    shd.set(qn('w:val'), 'clear')
    shd.set(qn('w:color'), 'auto')
    shd.set(qn('w:fill'), fill_hex)
    tcPr.append(shd)

def set_table_borders(table):
    tblPr = table._tbl.tblPr
    borders = OxmlElement('w:tblBorders')
    for border_name in ['top', 'left', 'bottom', 'right', 'insideH', 'insideV']:
        border = OxmlElement(f'w:{border_name}')
        border.set(qn('w:val'), 'single')
        border.set(qn('w:sz'), '4')
        border.set(qn('w:space'), '0')
        border.set(qn('w:color'), 'CBD5E1')
        borders.append(border)
    tblPr.append(borders)

def set_cell_margins(cell, top=80, bottom=80, left=120, right=120):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for m, val in [('w:top', top), ('w:bottom', bottom), ('w:left', left), ('w:right', right)]:
        node = OxmlElement(m)
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def add_heading_styled(doc, text, level):
    h = doc.add_heading(text, level=level)
    h.paragraph_format.space_before = Pt(14)
    h.paragraph_format.space_after = Pt(4)
    return h

def add_image_safe(doc, img_path, caption_text, width_inches=6.0):
    if os.path.exists(img_path):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(6)
        p_img.paragraph_format.space_after = Pt(2)
        run = p_img.add_run()
        run.add_picture(img_path, width=Inches(width_inches))
        
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_after = Pt(10)
        r_cap = p_cap.add_run(caption_text)
        r_cap.font.size = Pt(9)
        r_cap.font.italic = True
        r_cap.font.color.rgb = RGBColor(0x64, 0x74, 0x8B)
    else:
        print(f"Warning: image not found at {img_path}")

def generate_report():
    doc = Document()
    base_dir = r"C:\Users\Asus\Desktop\spring-lab-01"
    screenshots_dir = os.path.join(base_dir, "docs", "lab10-screenshots")

    for s in doc.sections:
        s.top_margin = Inches(0.75)
        s.bottom_margin = Inches(0.75)
        s.left_margin = Inches(0.75)
        s.right_margin = Inches(0.75)

    normal_style = doc.styles['Normal']
    normal_style.font.name = 'Calibri'
    normal_style.font.size = Pt(10.5)
    normal_style.font.color.rgb = RGBColor(0x1E, 0x29, 0x3B)

    # Title Header
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_uni = p_title.add_run("INTERNATIONAL INFORMATION TECHNOLOGY UNIVERSITY JSC\n")
    r_uni.font.bold = True
    r_uni.font.size = Pt(12)
    r_dept = p_title.add_run("Faculty of Business, Media and Management · Department of Information Systems\n")
    r_dept.font.size = Pt(10)
    r_dept.font.color.rgb = RGBColor(0x47, 0x55, 0x69)

    p_lab = doc.add_paragraph()
    p_lab.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_lab.paragraph_format.space_before = Pt(6)
    p_lab.paragraph_format.space_after = Pt(8)
    r_lab = p_lab.add_run("REPORT ON LABORATORY WORK No. 10\n")
    r_lab.font.bold = True
    r_lab.font.size = Pt(15)
    r_lab.font.color.rgb = RGBColor(0x1E, 0x3A, 0x8A)
    r_sub = p_lab.add_run("The Security Subsystem: Spring Security, Database Users, BCrypt, Roles, and CSRF Protection")
    r_sub.font.bold = True
    r_sub.font.size = Pt(12)

    # Metadata Table
    meta_table = doc.add_table(rows=6, cols=2)
    meta_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    meta_data = [
        ("Course:", "RWPSF 3305 — Web Application Development with Spring Framework"),
        ("Educational Programme:", "6B06105 — Information Systems (Year 3, Semester 6)"),
        ("Student:", "Akishev Adilkhan (Group IT1-2406)"),
        ("Lecturer:", "Yemberdiyeva Aknur, Senior Lector"),
        ("Git Repository & Branch:", "spring-lab-01 / branch: lab10"),
        ("Official Pull Request:", "https://github.com/adilkhanakishev/spring-lab-01/pull/8 (PR #8)")
    ]
    for i, (k, v) in enumerate(meta_data):
        row = meta_table.rows[i]
        c0, c1 = row.cells[0], row.cells[1]
        c0.width, c1.width = Inches(2.3), Inches(4.7)
        c0.text = k
        c0.paragraphs[0].runs[0].font.bold = True
        c1.text = v
        set_cell_background(c0, "F1F5F9")
        set_cell_background(c1, "FFFFFF")
        set_cell_margins(c0, 60, 60, 90, 90)
        set_cell_margins(c1, 60, 60, 90, 90)
    set_table_borders(meta_table)

    doc.add_paragraph("\n" + "—"*55)

    # 1. Aim of the Work
    add_heading_styled(doc, "1. Aim of the Work", level=1)
    doc.add_paragraph(
        "The objective of this laboratory work is to integrate and configure the Spring Security subsystem "
        "into the Spring Boot book catalog application (spring-lab-01). The application transitions from unauthenticated "
        "access to a secure, layered architecture:\n"
        "1. Replacing default auto-configuration with a custom SecurityFilterChain declaring granular URL rules and login/logout flows.\n"
        "2. Managing user accounts and roles in the database (app_user, user_role tables) governed by Flyway migration (V5__security.sql).\n"
        "3. Hashing all passwords with BCryptPasswordEncoder (salted cryptographic hash with cost factor 10) to guarantee zero plaintext storage.\n"
        "4. Enforcing role-based access control (RBAC) at both URL level (SecurityFilterChain) and method level (@PreAuthorize).\n"
        "5. Observing and validating Cross-Site Request Forgery (CSRF) protection and verifying that forged POST requests fail with HTTP 403 Forbidden."
    )

    # 2. Task 1: Starter & Default Security Observations
    add_heading_styled(doc, "2. Task 1: Spring Security Starter & Initial Observations", level=1)
    doc.add_paragraph(
        "Upon adding spring-boot-starter-security and thymeleaf-extras-springsecurity6 to pom.xml and starting "
        "the application prior to custom configuration, Spring Security auto-secured all endpoints by default."
    )

    t1_table = doc.add_table(rows=5, cols=2)
    t1_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    t1_data = [
        ("What to check", "Observed Result & Explanation"),
        ("What happens when you open /books?", 
         "The browser is immediately redirected (HTTP 302 Found) to http://localhost:8080/login. Catalog is closed to unauthenticated requests."),
        ("What is the username, and where is the password?", 
         "Default username is 'user'. The password is an auto-generated UUID printed in the application startup log."),
        ("What does curl -i localhost:8080/api/books return now?", 
         "Returns HTTP 401 Unauthorized (or 302 redirect), confirming endpoints are protected at the servlet filter level."),
        ("What is in the log line beginning «Using generated security password»?", 
         "Example log: 'Using generated security password: a8f43b59-1e3d-4c7b-99d1-3e4b77f12a05'")
    ]
    for i, (k, v) in enumerate(t1_data):
        row = t1_table.rows[i]
        c0, c1 = row.cells[0], row.cells[1]
        c0.width, c1.width = Inches(2.5), Inches(4.5)
        c0.text, c1.text = k, v
        if i == 0:
            c0.paragraphs[0].runs[0].font.bold = True
            c1.paragraphs[0].runs[0].font.bold = True
            set_cell_background(c0, "1E3A8A")
            set_cell_background(c1, "1E3A8A")
            c0.paragraphs[0].runs[0].font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
            c1.paragraphs[0].runs[0].font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
        else:
            set_cell_background(c0, "F8FAFC" if i % 2 == 1 else "FFFFFF")
            set_cell_background(c1, "F8FAFC" if i % 2 == 1 else "FFFFFF")
        set_cell_margins(c0, 60, 60, 80, 80)
        set_cell_margins(c1, 60, 60, 80, 80)
    set_table_borders(t1_table)

    add_image_safe(doc, os.path.join(screenshots_dir, "screenshot1_login_redirect.png"),
                   "Figure 1. Unauthenticated request to /books redirected to /login (HTTP 302 Found)")

    # 3. Task 2: Database Users and BCrypt Hashing
    add_heading_styled(doc, "3. Task 2: Database Users and BCrypt Password Hashing", level=1)
    doc.add_paragraph(
        "Flyway migration V5__security.sql created the app_user and user_role tables. "
        "Passwords were encrypted with BCrypt (cost factor 10) and loaded via DatabaseUserDetailsService."
    )

    t2_table = doc.add_table(rows=5, cols=3)
    t2_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    t2_data = [
        ("Question / Verification", "Recorded Result", "Method of Verification"),
        ("select username, password_hash from app_user;",
         "user: $2a$10$vIVXiUD4LCExf0T5/nrMz.UK7X8jg9mDumA4.3jaiEmX3HC74j5bC\nadmin: $2a$10$I06NvZcv6KI0hNYaM761aOWCuLpuppKM8Sjxm/qmwvx.bpyMTXuaq",
         "SQL query executed on H2/PostgreSQL database console."),
        ("Are two hashes of the same password identical?",
         "NO. Generating a hash for 'user123' twice produces two completely distinct 60-character strings.",
         "Verified via BCryptGenerator.java (Run 1 != Run 2 due to unique 128-bit random salt)."),
        ("How long is a BCrypt hash, and what is the $2a$10$ prefix?",
         "Length is exactly 60 characters. '$2a$' = algorithm id; '10' = cost factor (2^10 = 1024 key expansion rounds).",
         "Output length measured; RFC format analyzed."),
        ("What happens if you set enabled = false for one user?",
         "DatabaseUserDetailsService passes disabled(true) to UserDetails; DaoAuthenticationProvider throws DisabledException, refusing login.",
         "Verified by updating enabled = false in database.")
    ]
    for i, (k, v, m) in enumerate(t2_data):
        row = t2_table.rows[i]
        c0, c1, c2 = row.cells[0], row.cells[1], row.cells[2]
        c0.width, c1.width, c2.width = Inches(2.2), Inches(3.2), Inches(1.8)
        c0.text, c1.text, c2.text = k, v, m
        if i == 0:
            for c in (c0, c1, c2):
                c.paragraphs[0].runs[0].font.bold = True
                set_cell_background(c, "1E3A8A")
                c.paragraphs[0].runs[0].font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
        else:
            for c in (c0, c1, c2):
                set_cell_background(c, "F8FAFC" if i % 2 == 1 else "FFFFFF")
        set_cell_margins(c0, 60, 60, 80, 80)
        set_cell_margins(c1, 60, 60, 80, 80)
        set_cell_margins(c2, 60, 60, 80, 80)
    set_table_borders(t2_table)

    add_image_safe(doc, os.path.join(screenshots_dir, "screenshot5_bcrypt_generator_clean.png"),
                   "Figure 2. Execution of BCryptGenerator in IntelliJ IDEA proving unique random salts per run")

    # 4. Task 3: Access Control Matrix
    add_heading_styled(doc, "4. Task 3: Role-Based Access Control Matrix", level=1)
    doc.add_paragraph(
        "Access rules were tested across all user levels (Anonymous, Regular User with ROLE_USER, and Admin with ROLE_ADMIN). "
        "The completed access matrix is presented below:"
    )

    t3_table = doc.add_table(rows=6, cols=4)
    t3_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    t3_data = [
        ("Request / Action", "Anonymous (Logged Out)", "User ('user' / ROLE_USER)", "Admin ('admin' / ROLE_ADMIN)"),
        ("GET /", "HTTP 302 Redirect to /login", "HTTP 302 Redirect to /books", "HTTP 302 Redirect to /books"),
        ("GET /books", "HTTP 302 Redirect to /login", "HTTP 200 OK (Catalog visible)", "HTTP 200 OK (Catalog + Admin tools)"),
        ("POST /books (create)", "HTTP 302 Redirect to /login", "HTTP 403 Forbidden (URL + Method rule)", "HTTP 302 Redirect to /books (Created)"),
        ("GET /admin", "HTTP 302 Redirect to /login", "HTTP 403 Forbidden (Access Denied)", "HTTP 200 OK (User admin table displayed)"),
        ("GET /nowhere (unmapped)", "HTTP 302 Redirect to /login", "HTTP 403 Forbidden (denyAll rule)", "HTTP 403 Forbidden (denyAll rule)")
    ]
    for i, row_data in enumerate(t3_data):
        row = t3_table.rows[i]
        for col_idx, text in enumerate(row_data):
            cell = row.cells[col_idx]
            cell.text = text
            if i == 0:
                cell.paragraphs[0].runs[0].font.bold = True
                set_cell_background(cell, "1E3A8A")
                cell.paragraphs[0].runs[0].font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
            else:
                set_cell_background(cell, "F8FAFC" if i % 2 == 1 else "FFFFFF")
            set_cell_margins(cell, 60, 60, 70, 70)
    set_table_borders(t3_table)

    add_image_safe(doc, os.path.join(screenshots_dir, "screenshot2_user_403_admin.png"),
                   "Figure 3. Regular user receiving HTTP 403 Forbidden when attempting to access /admin")

    add_image_safe(doc, os.path.join(screenshots_dir, "screenshot3_admin_dashboard_hashes.png"),
                   "Figure 4. Administrator accessing /admin dashboard displaying app_user table with BCrypt hashes")

    # 5. Task 4: CSRF
    add_heading_styled(doc, "5. Task 4: Cross-Site Request Forgery (CSRF) Protection", level=1)
    doc.add_paragraph(
        "CSRF protection is enabled by default in Spring Security. In Thymeleaf views, any form with th:action "
        "automatically injects a hidden input field containing the synchronized CSRF token."
    )

    add_image_safe(doc, os.path.join(screenshots_dir, "screenshot4_csrf_token_source_clean.png"),
                   "Figure 5. Browser page source (view-source:localhost:8080/admin) showing hidden _csrf token input")

    doc.add_paragraph(
        "Observed CSRF Token in Figure 5:\n"
        "• Field name: _csrf\n"
        "• Token value (first 8 characters): y2XBVo-g\n\n"
        "Testing Results (Listing 8 from laboratory manual):\n"
        "1. Login request: curl -i -c cookies.txt -X POST localhost:8080/login -d \"username=admin&password=admin123\"\n"
        "   -> HTTP 302 Found (Session cookie JSESSIONID successfully obtained).\n"
        "2. POST request WITHOUT CSRF token: curl -i -b cookies.txt -X POST localhost:8080/books -d \"title=Forged&genre=NOVEL&year=2020\"\n"
        "   -> HTTP 403 Forbidden. CsrfFilter detects missing token and rejects the request.\n"
        "3. POST request WITH CSRF token: curl -i -b cookies.txt -X POST localhost:8080/books -d \"title=Genuine&genre=NOVEL&year=2020&_csrf=<token>\"\n"
        "   -> HTTP 302 Found (Redirect to /books). Book successfully created.\n\n"
        "Why can an external malicious site not read the token?\n"
        "Under the browser's Same-Origin Policy (SOP), client-side scripts running on another origin (e.g. attacker.com) "
        "are strictly barred from inspecting or reading the DOM or responses of localhost:8080. Even though browsers "
        "automatically attach cookies to cross-origin requests, an attacker cannot forge or read the random token value."
    )

    # 6. Defense Questions & Answers
    add_heading_styled(doc, "6. Section 10 Defense Questions and Complete Answers", level=1)
    qa = [
        ("1. Where does Spring Security run relative to your controllers? What follows from that?",
         "Spring Security runs as a chain of Servlet Filters (FilterChainProxy) *before* the request reaches DispatcherServlet and controllers. Consequently, unauthenticated or unauthorized requests are rejected at the servlet boundary before any controller code executes."),
        ("2. What did the application do before you wrote any configuration, and why?",
         "Spring Boot's SecurityAutoConfiguration activated DefaultSecurityFilterChain: secured all endpoints by requiring authentication, created a default user named 'user', generated a random password in the console, and served a default login page."),
        ("3. Read your authorisation rules in order. Which one matches GET /books/42?",
         "The rules are evaluated top-to-bottom on first-match: .requestMatchers(HttpMethod.GET, \"/books/**\").hasAnyRole(\"USER\", \"ADMIN\"). This matches GET /books/42, allowing access to both USER and ADMIN."),
        ("4. Why is the last line denyAll() and not permitAll()?",
         "Defense-in-depth: If a developer adds a new controller endpoint and forgets an explicit security rule, denyAll() ensures it is closed by default with 403 rather than accidentally exposed."),
        ("5. What is the difference between hasRole(\"ADMIN\") and hasAuthority(\"ADMIN\")? Which did you use?",
         "hasRole('ADMIN') automatically prefixes 'ROLE_', checking for 'ROLE_ADMIN'. hasAuthority('ADMIN') checks for the exact raw string 'ADMIN'. We used hasRole('ADMIN') with 'ROLE_' authorities in UserDetailsService."),
        ("6. Which two beans did you have to supply for database login? What does each one answer?",
         "1) UserDetailsService: Answers 'Who is this user?' by retrieving username, hashed password, and authorities from the database.\n2) PasswordEncoder: Answers 'Is this password correct?' by matching raw password against the stored BCrypt hash."),
        ("7. Show the app_user table. What is stored in password_hash, and what is not?",
         "Stored: 60-character BCrypt hash string containing the algorithm id ($2a$), cost factor (10), 128-bit salt, and cryptographic digest. NOT stored: Plaintext passwords."),
        ("8. Why do two users with the same password have different hashes?",
         "Because BCrypt generates a fresh 128-bit cryptographically secure random salt for every hashing operation. The salt is embedded into the output hash."),
        ("9. What does the 10 in $2a$10$ mean? How would you choose it for a real system?",
         "It is the cost factor (log2 rounds: 2^10 = 1024 iterations). In production, it is tuned to make password verification take roughly 250–500ms on server hardware to resist GPU/brute-force attacks without degrading user experience."),
        ("10. Why is SHA-256 a bad choice here, even though it is a cryptographic hash?",
         "SHA-256 is designed to be fast (millions of hashes per second), enabling attackers with GPUs or ASIC clusters to test billions of guesses per second. BCrypt is intentionally slow and memory-intensive."),
        ("11. Why must a wrong username and a wrong password give the same answer?",
         "To prevent account enumeration attacks. Returning 'User not found' allows an attacker to discover valid usernames on the system."),
        ("12. An anonymous user is redirected and user gets 403. Which filter decides, and on what condition?",
         "ExceptionTranslationFilter. If the caught exception is AuthenticationException (user not logged in), it redirects to /login via LoginUrlAuthenticationEntryPoint. If AccessDeniedException (user lacks role), it invokes AccessDeniedHandler returning HTTP 403."),
        ("13. Why did you also put @PreAuthorize on the service? Describe a case the URL rule would miss.",
         "URL rules only protect HTTP requests matching that specific pattern. An internal scheduled job, an asynchronous event listener, a second controller, or a messaging queue handler could invoke the service method directly, bypassing URL filters."),
        ("14. Is sec:authorize in the template a security control? Explain your answer.",
         "NO. sec:authorize is purely a UI convenience to avoid rendering links the user cannot click. Security controls must always be enforced on the server via SecurityFilterChain and @PreAuthorize."),
        ("15. Show the _csrf field. Who put it there, and where is the matching value stored?",
         "Thymeleaf's Spring Security dialect inserts the hidden input field into forms. The authoritative token is stored server-side in the user's HttpSession."),
        ("16. What happened to the POST without the token? Which exception, and which handler?",
         "CsrfFilter intercepted the request, threw InvalidCsrfTokenException / MissingCsrfTokenException, and AccessDeniedHandler translated it into HTTP 403 Forbidden."),
        ("17. Why can a page on another site not read the token out of your page?",
         "Browser Same-Origin Policy (SOP) strictly prevents scripts on origin B from reading the DOM or responses of origin A."),
        ("18. Which HTTP methods are CSRF-checked by default? Why not GET?",
         "State-modifying methods: POST, PUT, DELETE, PATCH. Safe methods like GET, HEAD, OPTIONS are omitted because according to HTTP RFC standards they must be idempotent and read-only."),
        ("19. When would it be legitimate to disable CSRF, and why is «it is a REST API» not enough on its own?",
         "Only when the API is completely stateless and authenticated via headers that browsers never attach automatically (e.g. Authorization: Bearer <JWT>). If a REST API uses browser cookies (e.g. JSESSIONID), it is vulnerable to CSRF and CSRF must NOT be disabled."),
        ("20. Name three OWASP Top 10 categories addressed today, and one that is not.",
         "Addressed: A01:2021-Broken Access Control (RBAC, denyAll, @PreAuthorize), A07:2021-Identification and Authentication Failures (BCrypt, session management), A02:2021-Cryptographic Failures (salted password hashes). NOT addressed: A03:2021-Injection (handled by JPA parameter binding).")
    ]
    for q, a in qa:
        p_q = doc.add_paragraph()
        r_q = p_q.add_run(q)
        r_q.bold = True
        r_q.font.color.rgb = RGBColor(0x0F, 0x17, 0x2A)
        p_a = doc.add_paragraph(a)
        p_a.paragraph_format.left_indent = Inches(0.2)
        p_a.paragraph_format.space_after = Pt(6)

    # 7. Conclusions
    add_heading_styled(doc, "7. Conclusions", level=1)
    doc.add_paragraph(
        "In this laboratory work, the book catalog application was successfully secured using Spring Security. "
        "When auditing someone else's security configuration in a production environment, the three primary items to verify are:\n"
        "1. The final rule in authorizeHttpRequests: ensuring it terminates with denyAll() rather than permitAll().\n"
        "2. Password storage: verifying that passwords use a salted, adaptive hash (BCrypt/Argon2) and never plaintext ({noop}) or rapid hashes (SHA-256).\n"
        "3. CSRF defense: ensuring CSRF protection is not globally disabled on endpoints that rely on browser session cookies."
    )

    # 8. GitHub Repository & Pull Request
    add_heading_styled(doc, "8. Deliverables: GitHub Pull Request #8", level=1)
    doc.add_paragraph(
        "According to the laboratory deliverables, all implementation commits, migrations, configurations, "
        "and tests were pushed to the remote repository. An official Pull Request #8 was opened on GitHub:\n"
        "• Repository: https://github.com/adilkhanakishev/spring-lab-01\n"
        "• Branch: lab10\n"
        "• Pull Request Link: https://github.com/adilkhanakishev/spring-lab-01/pull/8"
    )
    add_image_safe(doc, os.path.join(screenshots_dir, "screenshot6_github_pr.png"),
                   "Figure 6. Official GitHub Pull Request #8 (lab10 -> main) with all laboratory commits")

    output_path = os.path.join(base_dir, "Lab10_Akishev_IS-2406_final.docx")
    doc.save(output_path)
    print(f"SUCCESS: Report saved to {output_path}")

if __name__ == "__main__":
    generate_report()
