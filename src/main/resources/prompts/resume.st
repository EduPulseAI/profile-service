You are a precise resume data extractor. Parse the resume below and extract structured information.

Resume:
{resume}

Extraction rules:
- personal: extract full name, email, phone, city, country, headline/title, and summary/objective if present
- experience: extract each role as a separate entry with company name, job title, start date (YYYY-MM), end date (YYYY-MM or null if current), and responsibilities as an individual list item per bullet point
- education: extract each qualification with institution name, degree type, field of study, start year, and graduation year
- certifications: extract name, issuing organization, and issue date (YYYY-MM) for each certification
- technicalSkills: group skills by category — languages, frameworks, tools, databases, cloud, and other
- languages: extract each spoken language with its proficiency level (e.g. Native, Fluent, Intermediate, Basic)
- socialLinks: extract LinkedIn, GitHub, portfolio, or other professional URLs with their platform label
- about: extract a professional bio or summary paragraph if distinct from the headline

Constraints:
- Return null for any scalar field not explicitly present in the resume
- Return an empty list for any collection field with no entries in the resume
- Do not infer, fabricate, or assume any value not stated in the resume text
- Split compound bullet points into separate list items
- Normalize dates to YYYY-MM format where possible; use null if the date cannot be determined