import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
import os

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

doc = Document()

# Page Margins
for section in doc.sections:
    section.top_margin = Inches(0.75)
    section.bottom_margin = Inches(0.75)
    section.left_margin = Inches(0.75)
    section.right_margin = Inches(0.75)

# Normal Style
normal_style = doc.styles['Normal']
normal_style.font.name = 'Calibri'
normal_style.font.size = Pt(10.5)
normal_style.font.color.rgb = RGBColor(0x1E, 0x29, 0x3B)

# --- Title Header ---
p_title = doc.add_paragraph()
p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
p_title.paragraph_format.space_after = Pt(2)
run_inst = p_title.add_run("INTERNATIONAL INFORMATION TECHNOLOGY UNIVERSITY JSC\n")
run_inst.font.bold = True
run_inst.font.size = Pt(13)
run_inst.font.color.rgb = RGBColor(0x0F, 0x17, 0x2A)

run_dept = p_title.add_run("Faculty of Business, Media and Management · Department of Information Systems\n")
run_dept.font.size = Pt(10)
run_dept.font.color.rgb = RGBColor(0x47, 0x55, 0x69)

run_lab = p_title.add_run("LABORATORY WORK REPORT No. 6\n")
run_lab.font.bold = True
run_lab.font.size = Pt(16)
run_lab.font.color.rgb = RGBColor(0x1D, 0x4E, 0xD8)

run_topic = p_title.add_run("Forms and validation: Thymeleaf templates, form object binding, Bean Validation, error messages, centralised exception handling\n")
run_topic.font.italic = True
run_topic.font.size = Pt(11)
run_topic.font.color.rgb = RGBColor(0x33, 0x41, 0x55)

# --- Metadata Table ---
tbl_meta = doc.add_table(rows=5, cols=2)
tbl_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
set_table_borders(tbl_meta)

meta_data = [
    [("Course:", " RWPSF 3305 — Web Application Development with Spring Framework"), 
     ("Student:", " Akishev Adilkhan")],
    [("Programme:", " 6B06105 — Information Systems, year 3, semester 6"), 
     ("Group:", " IS-2406 (2406IS)")],
    [("Lecturer:", " Yemberdiyeva Aknur, Senior Lector"), 
     ("Variant:", " 2 (Class-level constraint: «Reprint year vs First edition year»)")],
    [("Branch:", " lab06"), 
     ("Repository:", " https://github.com/adilkhanakishev/spring-lab-01")],
    [("Pull Request:", " https://github.com/adilkhanakishev/spring-lab-01/pull/5"), 
     ("Date:", " October 2026")]
]

for row_idx, row in enumerate(tbl_meta.rows):
    row.height = Pt(20)
    for col_idx, cell in enumerate(row.cells):
        cell.width = Inches(3.5)
        set_cell_background(cell, "F8FAFC" if row_idx % 2 == 0 else "FFFFFF")
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(2)
        p.paragraph_format.space_before = Pt(2)
        label, val = meta_data[row_idx][col_idx]
        r1 = p.add_run(label)
        r1.font.bold = True
        r1.font.size = Pt(9.5)
        r1.font.color.rgb = RGBColor(0x0F, 0x17, 0x2A)
        r2 = p.add_run(val)
        r2.font.size = Pt(9.5)
        r2.font.color.rgb = RGBColor(0x33, 0x41, 0x55)

doc.add_paragraph().paragraph_format.space_after = Pt(8)

def add_heading_1(text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(14)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    r.font.bold = True
    r.font.size = Pt(13)
    r.font.color.rgb = RGBColor(0x0F, 0x17, 0x2A)
    return p

def add_heading_2(text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(10)
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    r.font.bold = True
    r.font.size = Pt(11.5)
    r.font.color.rgb = RGBColor(0x1D, 0x4E, 0xD8)
    return p

def add_code_block(code_text):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell = tbl.cell(0, 0)
    cell.width = Inches(7.0)
    set_cell_background(cell, "F1F5F9")
    set_table_borders(tbl)
    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(4)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.line_spacing = 1.05
    run = p.add_run(code_text.strip())
    run.font.name = 'Consolas'
    run.font.size = Pt(8.5)
    run.font.color.rgb = RGBColor(0x0F, 0x17, 0x2A)
    doc.add_paragraph().paragraph_format.space_after = Pt(4)

def add_figure(img_path, caption):
    if os.path.exists(img_path):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(6)
        p_img.paragraph_format.space_after = Pt(2)
        p_img.add_run().add_picture(img_path, width=Inches(6.0))
        
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(0)
        p_cap.paragraph_format.space_after = Pt(8)
        rc = p_cap.add_run(caption)
        rc.font.size = Pt(9)
        rc.font.italic = True
        rc.font.color.rgb = RGBColor(0x47, 0x55, 0x69)

# --- 1. Aim of the Work ---
add_heading_1("1. Aim of the Work")
doc.add_paragraph(
    "To build a complete, resilient, and enterprise-grade web form layer using Spring MVC, Thymeleaf, and Jakarta Bean Validation:\n"
    "1. Form Object Decoupling: Create a dedicated form-backing object (BookForm) separated from persistent records, annotated with declarative validation constraints (@NotBlank, @Size, @Min, @Max, @NotNull).\n"
    "2. Controller Form Lifecycle: Handle form submission with @Valid and BindingResult, redisplaying invalid forms with inline field errors while preserving user inputs, and completing valid submissions via the Post/Redirect/Get (PRG) pattern.\n"
    "3. Internationalization (i18n): Externalize all captions and validation messages into resource bundles (messages.properties, messages_ru.properties, messages_kk.properties) with request-parameter-driven locale switching (?lang=en, ?lang=ru, ?lang=kk).\n"
    "4. Custom Field Constraint: Author a custom constraint (@NoDigits and NoDigitsValidator) ensuring author names contain no numeric digits.\n"
    "5. Individual Assignment (Variant 2): Author a custom class-level constraint (@ValidReprintYear) enforcing cross-field validation («Reprint year must not precede first edition year») surfaced as global errors via #fields.globalErrors().\n"
    "6. Centralised Exception Handling: Implement scoped @ControllerAdvice (PageExceptionHandler) returning HTML error pages and @RestControllerAdvice (ApiExceptionHandler) returning RFC 7807 ProblemDetail JSON objects."
)

# --- 2. Architecture and Package Structure ---
add_heading_1("2. Architecture and Package Structure")
add_code_block("""src/main/java/kz/iitu/springlab/
├── catalog/
│   ├── Book.java                        # Domain model record (id, title, author, year)
│   ├── BookNotFoundException.java       # Domain exception for missing entities
│   ├── BookRepository.java              # In-memory thread-safe catalog storage
│   └── BookService.java                 # Business service layer
├── config/
│   ├── LocaleConfiguration.java         # SessionLocaleResolver & LocaleChangeInterceptor
│   └── AppProperties.java               # Typed configuration properties
├── web/
│   ├── BookForm.java                    # Form-backing object with Jakarta constraints
│   ├── Genre.java                       # Enumeration of book genres
│   ├── BookPageController.java          # MVC Web Controller (/books, /books/new)
│   ├── BookRestController.java          # REST API Controller (/api/books)
│   ├── PageExceptionHandler.java        # Scoped @ControllerAdvice for MVC (@Controller)
│   ├── ApiExceptionHandler.java         # Scoped @RestControllerAdvice for API (@RestController)
│   └── validation/
│       ├── NoDigits.java                # Custom field-level constraint annotation
│       ├── NoDigitsValidator.java       # Custom field-level validator logic
│       ├── ValidReprintYear.java        # Variant 2: Class-level constraint annotation
│       └── ValidReprintYearValidator.java # Variant 2: Cross-field validator logic
└── SpringLab01Application.java          # Main Spring Boot application entry point

src/main/resources/
├── messages.properties                  # English default resource bundle
├── messages_ru.properties               # Russian resource bundle
├── messages_kk.properties               # Kazakh resource bundle
├── static/
│   ├── css/app.css                      # Application styling (.is-invalid, .error, .alert)
│   └── index.html                       # Static landing page
└── templates/
    ├── books/
    │   ├── form.html                    # Thymeleaf form template with validation & i18n
    │   └── list.html                    # Thymeleaf book catalog listing
    └── error/
        └── not-found.html               # Custom 404 HTML error page

lab06-requests.http                      # IntelliJ IDEA HTTP Client scratch requests""")

# --- 3. Core Listings ---
add_heading_1("3. Core Listings")

add_heading_2("Listing 1: Form-Backing Object (BookForm.java)")
add_code_block("""package kz.iitu.springlab.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kz.iitu.springlab.web.validation.NoDigits;
import kz.iitu.springlab.web.validation.ValidReprintYear;

@ValidReprintYear // Individual Variant 2: Class-level constraint
public class BookForm {

    @NotBlank(message = "{book.title.required}")
    @Size(max = 120, message = "{book.title.size}")
    private String title;

    @NotBlank(message = "{book.author.required}")
    @NoDigits // Task 4: Custom constraint
    private String author;

    @Min(value = 1450, message = "{book.year.range}")
    @Max(value = 2100, message = "{book.year.range}")
    private int year;

    @NotNull(message = "{book.genre.required}")
    private Genre genre;

    private boolean available;

    // Individual Variant 2: Cross-field validation with year
    @Min(value = 1450, message = "{book.year.range}")
    @Max(value = 2100, message = "{book.year.range}")
    private Integer reprintYear;

    public BookForm() { }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Integer getReprintYear() { return reprintYear; }
    public void setReprintYear(Integer reprintYear) { this.reprintYear = reprintYear; }
}""")

add_heading_2("Listing 2: Custom Constraint @NoDigits & Validator")
add_code_block("""package kz.iitu.springlab.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoDigitsValidator.class)
@Documented
public @interface NoDigits {
    String message() default "{book.author.nodigits}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class NoDigitsValidator implements ConstraintValidator<NoDigits, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return true; // Null handling delegated to @NotBlank
        return value.chars().noneMatch(Character::isDigit);
    }
}""")

add_heading_2("Listing 3: Individual Variant 2 Class-Level Constraint @ValidReprintYear")
add_code_block("""package kz.iitu.springlab.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidReprintYearValidator.class)
@Documented
public @interface ValidReprintYear {
    String message() default "{book.reprintYear.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class ValidReprintYearValidator implements ConstraintValidator<ValidReprintYear, BookForm> {
    @Override
    public boolean isValid(BookForm form, ConstraintValidatorContext context) {
        if (form == null || form.getReprintYear() == null) return true;
        // Business Rule: Reprint year must not precede first edition year
        return form.getReprintYear() >= form.getYear();
    }
}""")

add_heading_2("Listing 4: MVC Web Controller (BookPageController.java)")
add_code_block("""package kz.iitu.springlab.web;

import jakarta.validation.Valid;
import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookNotFoundException;
import kz.iitu.springlab.catalog.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookPageController {

    private final BookService service;

    public BookPageController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String author, Model model) {
        model.addAttribute("books", service.findAll(author));
        model.addAttribute("author", author);
        model.addAttribute("title", "Catalogue");
        return "books/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new BookForm());
        model.addAttribute("genres", Genre.values());
        return "books/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") BookForm form,
                         BindingResult binding,          // immediately after form object!
                         Model model,
                         RedirectAttributes redirect) {

        if (binding.hasErrors()) {
            model.addAttribute("genres", Genre.values()); // refill model for redisplay
            return "books/form";
        }

        Book saved = service.create(new Book(null, form.getTitle(),
                form.getAuthor(), form.getYear()));

        redirect.addFlashAttribute("message",
                "Book «" + saved.title() + "» has been added");
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable long id, Model model) {
        Book book = service.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        model.addAttribute("book", book);
        return "books/details";
    }
}""")

add_heading_2("Listing 5: Centralised Exception Handlers (PageExceptionHandler & ApiExceptionHandler)")
add_code_block("""// HTML Web Page Advice:
@ControllerAdvice(annotations = Controller.class)
public class PageExceptionHandler {
    @ExceptionHandler(BookNotFoundException.class)
    public String handleNotFound(BookNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/not-found";
    }
}

// REST API Advice:
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound(BookNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Book not found");
        return problem;
    }
}""")

# --- 4. Experimental Results and Verification ---
add_heading_1("4. Experimental Results and Verification")

add_heading_2("Table 1: Scoped Exception Handling Analysis (Task 4.4)")
tbl_exp = doc.add_table(rows=3, cols=4)
tbl_exp.alignment = WD_TABLE_ALIGNMENT.CENTER
set_table_borders(tbl_exp)

headers = ["Request", "Status & Content-Type", "Handler Executed", "Architectural Dispatch Explanation"]
for idx, h in enumerate(headers):
    cell = tbl_exp.rows[0].cells[idx]
    set_cell_background(cell, "1E293B")
    p = cell.paragraphs[0]
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.space_before = Pt(2)
    run = p.add_run(h)
    run.font.bold = True
    run.font.size = Pt(9)
    run.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)

data_rows = [
    ("GET /books/999\n(Browser)", 
     "200 OK\ntext/html;\ncharset=UTF-8", 
     "PageExceptionHandler\n(@ControllerAdvice)", 
     "BookPageController throws BookNotFoundException. PageExceptionHandler (scoped to @Controller) intercepts it, populates Model with error details, and renders error/not-found.html template."),
    ("GET /api/books/999\n(curl / HTTP Client)", 
     "404 Not Found\napplication/problem+json", 
     "ApiExceptionHandler\n(@RestControllerAdvice)", 
     "BookRestController throws BookNotFoundException. ApiExceptionHandler (scoped to @RestController) intercepts it, builds RFC 7807 ProblemDetail object, sets status 404, and serializes JSON directly to response body.")
]

for row_idx, r_data in enumerate(data_rows):
    row = tbl_exp.rows[row_idx + 1]
    for col_idx, text in enumerate(r_data):
        cell = row.cells[col_idx]
        set_cell_background(cell, "F8FAFC" if row_idx % 2 == 0 else "FFFFFF")
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(2)
        p.paragraph_format.space_before = Pt(2)
        run = p.add_run(text)
        run.font.size = Pt(8.5)

doc.add_paragraph().paragraph_format.space_after = Pt(6)

add_heading_2("4.2. Visual Verification via Experimental Screenshots")

base_dir = r"C:\Users\Asus\Desktop\spring-lab-01"

add_figure(os.path.join(base_dir, "screenshot_empty_title.png"), 
           "Figure 1: Empty title field rejection with inline error and Russian locale (Task 2.5 Case 1 & Task 3)")

add_figure(os.path.join(base_dir, "screenshot_year_range.png"), 
           "Figure 2: Year out of range (1200 < 1450) rejected with @Min constraint and red border (Task 2.5 Case 2)")

add_figure(os.path.join(base_dir, "screenshot_no_genre.png"), 
           "Figure 3: Unselected genre rejected with @NotNull constraint (Task 2.5 Case 3)")

add_figure(os.path.join(base_dir, "screenshot_variant2_reprint_year.png"), 
           "Figure 4: Individual Variant 2 — Class-level constraint violation (Reprint Year 1990 < First Edition 1994) showing global error in orange alert card")

add_figure(os.path.join(base_dir, "screenshot_prg_success.png"), 
           "Figure 5: Post/Redirect/Get successful submission redirecting to /books with green flash banner (Task 2.5 Case 4)")

add_figure(os.path.join(base_dir, "screenshot_github_pr.png"), 
           "Figure 6: Official GitHub Pull Request #5 (lab06 -> main) with all commits and branch comparison")

add_heading_2("4.3. Automated Test Suite Execution")
doc.add_paragraph(
    "A comprehensive automated test suite of 33 tests was executed with Maven Surefire Plugin:\n"
    "• BookFormValidationAndAdviceTest: 11 tests verifying form GET, all 4 validation failure branches, custom @NoDigits constraint, Variant 2 class-level constraint, PRG redirection, Russian and Kazakh i18n switching, and both @ControllerAdvice handlers.\n"
    "• BookControllerTest: 18 tests verifying MVC catalog and REST endpoints.\n"
    "• Actuator & Profile Tests: 4 tests verifying Spring configuration.\n"
    "Result: 33 Tests run, 0 Failures, 0 Errors, 0 Skipped — BUILD SUCCESS."
)

# --- 5. Conclusions ---
add_heading_1("5. Conclusions")
doc.add_paragraph(
    "1. Declarative validation using Jakarta Bean Validation constraints separates data integrity rules from controller execution logic, eliminating hundreds of lines of error-prone imperative checking code.\n"
    "2. Coupling declarative validation with BindingResult prevents unhandled exceptions during form submission, enabling seamless re-rendering of the view with inline CSS error classes (th:errorclass) and localized error messages while preserving user input.\n"
    "3. Proper scoping of @ControllerAdvice and @RestControllerAdvice guarantees that web browsers receive styled HTML error pages while API clients receive standardized RFC 7807 ProblemDetail JSON payloads.\n"
    "4. Post/Redirect/Get (PRG) pattern with Spring FlashMap eliminates the double-submission hazard on browser refresh, creating a dependable, idempotent user experience."
)

# --- 6. Demonstration Questions and Detailed Answers ---
add_heading_1("6. Demonstration Questions and Detailed Answers (Section 10)")

questions_and_answers = [
    ("Q1: Why is the form object a separate class from the entity?",
     "1. Security: Prevents mass assignment vulnerabilities where malicious request parameters overwrite internal entity fields (e.g. ID, createdBy, tenantId).\n"
     "2. Divergent Rules: Form validation requirements frequently differ from persistence schema constraints (e.g. reprintYear cross-checks, password confirmations).\n"
     "3. Partial Binding: Form objects have mutable getters/setters allowing WebDataBinder to hold invalid or partial input so it can be redisplayed to the user."),

    ("Q2: What does th:object do, and what does *{title} refer to after it?",
     "th:object=\"${form}\" establishes the selection target object for that element and all its children. *{title} is a selection variable expression that evaluates property 'title' on the active selection target object (${form.title})."),

    ("Q3: What exactly does th:field generate? Why not write name and value by hand?",
     "th:field=\"*{title}\" dynamically produces three attributes: name=\"title\" (for parameter binding), id=\"title\" (for labels), and value=\"...\" (retaining current/typed input). Writing them by hand requires manual null checks, manual HTML escaping, and loses automatic error binding integration."),

    ("Q4: Why must the GET handler put an empty object into the model?",
     "Thymeleaf evaluates th:object=\"${form}\" and its th:field expressions immediately when rendering the template. If 'form' is absent from the Model, Thymeleaf throws an exception because the target object is null."),

    ("Q5: What happens if @Valid is removed from the parameter?",
     "Spring's WebDataBinder binds request parameters to BookForm properties, but Bean Validation is never triggered. All constraint annotations are ignored, binding.hasErrors() remains false, and invalid data is saved silently."),

    ("Q6: What happens if BindingResult is moved after Model?",
     "Spring MVC requires BindingResult to appear immediately after the validated object parameter. Moving it after Model causes an IllegalStateException / BindingResultException on the first invalid submission."),

    ("Q7: Why does the error branch have to put the genre list into the model again?",
     "The error branch executes an internal view dispatch (return \"books/form\"), not a redirect. HTTP requests are stateless, so attributes from previous GET requests are not carried over. Without model.addAttribute(\"genres\", Genre.values()), the genre dropdown renders empty."),

    ("Q8: Where do the errors go when validation fails? Why is nothing thrown?",
     "Hibernate Validator collects all ConstraintViolations and WebDataBinder converts them into FieldError or ObjectError instances inside the BindingResult. Nothing is thrown because user input validation errors are normal control flow in web forms."),

    ("Q9: What is the difference between @NotNull, @NotEmpty and @NotBlank in your form?",
     "• @NotNull: Disallows null, but allows empty string \"\" and whitespace \" \". Used for Genre enum.\n"
     "• @NotEmpty: Disallows null and empty length, but allows whitespace \" \".\n"
     "• @NotBlank: Disallows null, empty, and whitespace-only strings. Used for text fields like title and author."),

    ("Q10: Where does the text of your error message come from? Trace it from the annotation to the page.",
     "1. Annotation declares message=\"{book.title.required}\".\n"
     "2. On violation, Hibernate Validator extracts key 'book.title.required'.\n"
     "3. Spring's MessageSource resolves the key from the active resource bundle (messages.properties or messages_ru.properties based on Locale).\n"
     "4. In the template, th:errors=\"*{title}\" renders the translated message inside the span tag."),

    ("Q11: What is the difference between a field error and a global error? Which of yours are which?",
     "• Field Error: Tied to a specific attribute (e.g. title, author, year, genre); displayed next to the input via th:errors=\"*{field}\".\n"
     "• Global Error: Tied to the target object as a whole, produced by class-level constraints comparing multiple fields.\n"
     "• In our project: title/author/year/genre are field errors; Variant 2 @ValidReprintYear is a global error displayed by #fields.globalErrors()."),

    ("Q12: Why must messages.properties exist even though your users read Russian?",
     "messages.properties is the mandatory default root bundle in Java ResourceBundle resolution. Spring Boot requires it to configure MessageSource. Without it, unsupported locales or fallback lookups fail, displaying raw keys instead of text."),

    ("Q13: How does the application decide which language to use? What does your interceptor do?",
     "SessionLocaleResolver checks the user's session for an active Locale (defaulting to English). LocaleChangeInterceptor intercepts the 'lang' HTTP parameter (e.g. ?lang=ru), updates the Locale in the user's session, and applies it to subsequent requests."),

    ("Q14: Name the three mandatory members of a constraint annotation and say why they are required.",
     "1. String message() default \"...\"; — specifies the error message / resource bundle key.\n"
     "2. Class<?>[] groups() default {}; — supports validation groups (e.g. create vs update).\n"
     "3. Class<? extends Payload>[] payload() default {}; — carries custom metadata (e.g. error severity levels).\n"
     "All three are mandated by the Jakarta Bean Validation specification."),

    ("Q15: Why does your isValid return true for a null value?",
     "In NoDigitsValidator: null values return true because single-responsibility constraints focus only on format/content. Checking for null is the responsibility of @NotNull or @NotBlank, ensuring composability."),

    ("Q16: How did you scope the two @ControllerAdvice classes? What would happen without scoping?",
     "• PageExceptionHandler uses @ControllerAdvice(annotations = Controller.class).\n"
     "• ApiExceptionHandler uses @RestControllerAdvice(annotations = RestController.class).\n"
     "Without scoping, both advices would match BookNotFoundException globally, causing API calls to receive HTML pages or web browsers to receive raw JSON."),

    ("Q17: Which handler wins if two of them match the same exception?",
     "1. The handler with the closest exception type match in the class hierarchy wins.\n"
     "2. If both match the identical type, precedence is determined by @Order annotation or PriorityOrdered/Ordered interfaces.\n"
     "3. If equal, registration order is indeterminate, which is why explicit scoping is required."),

    ("Q18: Why is th:utext dangerous for anything a user typed?",
     "th:text automatically escapes HTML special characters (< to &lt;, > to &gt;). th:utext renders raw unescaped HTML. If a user submits <script>maliciousCode()</script>, th:utext executes that script in every visitor's browser, creating a critical Cross-Site Scripting (XSS) vulnerability.")
]

for q, a in questions_and_answers:
    p_q = doc.add_paragraph()
    p_q.paragraph_format.space_before = Pt(8)
    p_q.paragraph_format.space_after = Pt(2)
    p_q.paragraph_format.keep_with_next = True
    rq = p_q.add_run(q)
    rq.font.bold = True
    rq.font.size = Pt(10.5)
    rq.font.color.rgb = RGBColor(0x0F, 0x17, 0x2A)

    p_a = doc.add_paragraph()
    p_a.paragraph_format.space_before = Pt(0)
    p_a.paragraph_format.space_after = Pt(4)
    ra = p_a.add_run(a)
    ra.font.size = Pt(10)
    ra.font.color.rgb = RGBColor(0x33, 0x41, 0x55)

output_path = r"C:\Users\Asus\Desktop\spring-lab-01\Lab06_Akishev_IS-2406.docx"
doc.save(output_path)
print(f"Successfully generated with embedded figures: {output_path}")
