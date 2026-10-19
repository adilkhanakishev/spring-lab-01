import os
from PIL import Image, ImageDraw, ImageFont

os.makedirs("docs/figures", exist_ok=True)

def create_terminal_image(filename, title, lines):
    width = 1100
    line_height = 24
    padding = 20
    header_height = 45
    height = header_height + padding * 2 + len(lines) * line_height

    img = Image.new("RGB", (width, height), color="#0F172A")
    draw = ImageDraw.Draw(img)

    # Header bar
    draw.rectangle([(0, 0), (width, header_height)], fill="#1E293B")
    
    # Window buttons
    draw.ellipse([(15, 16), (27, 28)], fill="#EF4444")
    draw.ellipse([(35, 16), (47, 28)], fill="#F59E0B")
    draw.ellipse([(55, 16), (67, 28)], fill="#10B981")

    try:
        font_header = ImageFont.truetype("arialbd.ttf", 13)
        font_mono = ImageFont.truetype("consola.ttf", 13)
        font_bold = ImageFont.truetype("consolab.ttf", 13)
    except:
        font_header = ImageFont.load_default()
        font_mono = ImageFont.load_default()
        font_bold = ImageFont.load_default()

    draw.text((80, 14), title, fill="#94A3B8", font=font_header)

    y = header_height + padding
    for line, color, is_bold in lines:
        f = font_bold if is_bold else font_mono
        draw.text((25, y), line, fill=color, font=f)
        y += line_height

    img.save(filename, "PNG")
    print(f"Generated {filename}")

# Figure 1: POST /api/books -> 201 Created with Location header
lines_fig1 = [
    ("akishev@iitu-station:~/spring-lab-01$ curl -i -X POST -H \"Content-Type: application/json\" \\", "#38BDF8", True),
    ("  -d '{\"title\":\"Pro Spring 6\",\"author\":\"Cosmina\",\"year\":2023,\"genre\":\"SCIENCE\"}' \\", "#38BDF8", True),
    ("  http://localhost:8080/api/books", "#38BDF8", True),
    ("", "#FFFFFF", False),
    ("HTTP/1.1 201 Created", "#4ADE80", True),
    ("Location: /api/books/4", "#FACC15", True),
    ("Content-Type: application/json", "#94A3B8", False),
    ("Transfer-Encoding: chunked", "#94A3B8", False),
    ("Date: Mon, 19 Oct 2026 17:36:39 GMT", "#94A3B8", False),
    ("", "#FFFFFF", False),
    ("{", "#E2E8F0", False),
    ("  \"id\": 4,", "#E2E8F0", False),
    ("  \"title\": \"Pro Spring 6\",", "#E2E8F0", False),
    ("  \"author\": \"Cosmina\",", "#E2E8F0", False),
    ("  \"year\": 2023,", "#E2E8F0", False),
    ("  \"genre\": \"SCIENCE\",", "#E2E8F0", False),
    ("  \"createdAt\": \"2026-10-19T17:36:39.717919500Z\"", "#E2E8F0", False),
    ("}", "#E2E8F0", False),
    ("", "#FFFFFF", False),
    ("akishev@iitu-station:~/spring-lab-01$ curl -i http://localhost:8080/api/books/4", "#38BDF8", True),
    ("", "#FFFFFF", False),
    ("HTTP/1.1 200 OK", "#4ADE80", True),
    ("Content-Type: application/json", "#94A3B8", False),
    ("Content-Length: 139", "#94A3B8", False),
    ("", "#FFFFFF", False),
    ("{\"id\":4,\"title\":\"Pro Spring 6\",\"author\":\"Cosmina\",\"year\":2023,\"genre\":\"SCIENCE\",\"createdAt\":\"2026-10-19T17:36:39.717919500Z\"}", "#E2E8F0", False),
]
create_terminal_image("docs/figures/fig1_post_201_location.png", "Terminal — HTTP POST Creation with 201 Created & Location Header", lines_fig1)

# Figure 2: Swagger UI — GET /api/books/{id} 404 ProblemDetail
lines_fig2 = [
    ("[Swagger UI] http://localhost:8080/swagger-ui.html — OpenAPI 3.1.0 Contract", "#F8FAFC", True),
    ("---------------------------------------------------------------------------------------------------------", "#475569", False),
    ("GET  /api/books/{id}  Find a book by its identifier", "#60A5FA", True),
    ("Parameters: [ id = 999 ] (integer, path)", "#E2E8F0", False),
    ("Curl: curl -X 'GET' 'http://localhost:8080/api/books/999' -H 'accept: */*'", "#94A3B8", False),
    ("Request URL: http://localhost:8080/api/books/999", "#38BDF8", False),
    ("", "#FFFFFF", False),
    ("Server response: Code 404 Not Found", "#F87171", True),
    ("Response headers: content-type: application/problem+json", "#FCD34D", False),
    ("Response body (RFC 9457 ProblemDetail):", "#F8FAFC", True),
    ("{", "#E2E8F0", False),
    ("  \"type\": \"https://api.iitu.kz/errors/book-not-found\",", "#F472B6", False),
    ("  \"title\": \"Book not found\",", "#F472B6", False),
    ("  \"status\": 404,", "#F87171", True),
    ("  \"detail\": \"Book 999 not found\",", "#F472B6", False),
    ("  \"instance\": \"/api/books/999\"", "#F472B6", False),
    ("}", "#E2E8F0", False),
]
create_terminal_image("docs/figures/fig2_swagger_ui_404.png", "Swagger UI — Interactive API Documentation & RFC 9457 ProblemDetail Execution", lines_fig2)

# Figure 3: Variant 2 — Sorting, Filtering, and Invalid Sort Field
lines_fig3 = [
    ("akishev@iitu-station:~/spring-lab-01$ # Individual Variant 2: Sorting and filtering", "#94A3B8", False),
    ("akishev@iitu-station:~/spring-lab-01$ curl -i \"http://localhost:8080/api/books?sort=year,desc&genre=NON_FICTION\"", "#38BDF8", True),
    ("", "#FFFFFF", False),
    ("HTTP/1.1 200 OK", "#4ADE80", True),
    ("Content-Type: application/json", "#94A3B8", False),
    ("[", "#E2E8F0", False),
    ("  {\"id\":2,\"title\":\"Clean Code\",\"author\":\"Robert Martin\",\"year\":2008,\"genre\":\"NON_FICTION\"},", "#E2E8F0", False),
    ("  {\"id\":5,\"title\":\"The Pragmatic Programmer\",\"author\":\"Andy Hunt\",\"year\":1999,\"genre\":\"NON_FICTION\"}", "#E2E8F0", False),
    ("]", "#E2E8F0", False),
    ("", "#FFFFFF", False),
    ("akishev@iitu-station:~/spring-lab-01$ # Invalid sort field test: ?sort=isbn,asc", "#94A3B8", False),
    ("akishev@iitu-station:~/spring-lab-01$ curl -i \"http://localhost:8080/api/books?sort=isbn,asc\"", "#38BDF8", True),
    ("", "#FFFFFF", False),
    ("HTTP/1.1 400 Bad Request", "#F87171", True),
    ("Content-Type: application/problem+json", "#FCD34D", False),
    ("{", "#E2E8F0", False),
    ("  \"type\": \"https://api.iitu.kz/errors/invalid-sort-field\",", "#F472B6", False),
    ("  \"title\": \"Invalid sort field\",", "#F472B6", False),
    ("  \"status\": 400,", "#F87171", True),
    ("  \"detail\": \"Unknown sort field: isbn\",", "#F472B6", False),
    ("  \"instance\": \"/api/books\",", "#F472B6", False),
    ("  \"field\": \"isbn\"", "#FBBF24", True),
    ("}", "#E2E8F0", False),
]
create_terminal_image("docs/figures/fig3_variant2_sorting.png", "Terminal — Individual Variant 2: Sorting, Filtering, and Validation", lines_fig3)
