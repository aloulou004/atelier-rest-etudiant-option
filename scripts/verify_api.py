"""Exercise the deployed REST API with only the Python standard library."""

import json
import sys
import urllib.error
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET

BASE = sys.argv[1].rstrip("/") if len(sys.argv) > 1 else \
    "http://localhost:8080/Gestion_Options_Etudiants/rest"


def request(method, path, body=None, accept="application/json"):
    data = json.dumps(body).encode("utf-8") if body is not None else None
    req = urllib.request.Request(
        BASE + path, data=data, method=method,
        headers={"Accept": accept, "Content-Type": "application/json"},
    )
    try:
        response = urllib.request.urlopen(req)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        payload = response.read().decode("utf-8")
        return response.status, response.headers.get("Content-Type", ""), payload


def check(method, path, expected, body=None, accept="application/json"):
    status, content_type, payload = request(method, path, body, accept)
    assert status == expected, (method, path, status, expected, payload[:300])
    print(f"{method:6} {path:35} {status}")
    return content_type, payload


def main():
    option = {"codeOption": 99, "libelle": "Test", "domaine": "Sciences",
              "responsable": "Responsable test", "credits": 20, "semestre": 2,
              "capacite": 20}
    student = {"identifiant": "I999", "nom": "Test", "prenom": "API",
               "option": {"codeOption": 99}, "anneeEtude": 2026,
               "email": "test@example.com"}

    # Remove leftovers if this script was interrupted during an earlier run.
    request("DELETE", "/etudiants/I999")
    request("DELETE", "/options/99")
    try:
        kind, payload = check("GET", "/options", 200)
        assert "application/json" in kind and len(json.loads(payload)) >= 5
        _, payload = check("GET", "/options?domaine=Informatique", 200)
        assert all(o["domaine"] == "Informatique" for o in json.loads(payload))
        check("GET", "/options/1", 200)
        check("GET", "/options/9999", 404)
        check("POST", "/options", 200, option)
        check("POST", "/options", 409, option)
        option["capacite"] = 35
        check("PUT", "/options/99", 200, option)
        _, payload = check("GET", "/options/99", 200)
        assert json.loads(payload)["capacite"] == 35

        kind, payload = check("GET", "/etudiants", 200)
        assert "application/json" in kind and len(json.loads(payload)) >= 3
        _, payload = check("GET", "/etudiants/I001", 200)
        assert json.loads(payload)["option"]["codeOption"] == 1
        check("GET", "/etudiants/absent", 404)
        kind, payload = check("GET", "/etudiants/option?codeOption=1", 200,
                              accept="application/xml")
        assert "application/xml" in kind
        root = ET.fromstring(payload)
        assert root.tag == "etudiants" and len(root.findall("etudiant")) == 2
        assert root.find("etudiant/option") is None
        check("GET", "/etudiants/option?codeOption=9999", 404,
              accept="application/xml")
        check("POST", "/etudiants", 404,
              dict(student, option={"codeOption": 9999}))
        check("POST", "/etudiants", 200, student)
        check("POST", "/etudiants", 409, student)
        student["nom"] = "Modifie"
        check("PUT", "/etudiants/I999", 200, student)
        _, payload = check("GET", "/etudiants/I999", 200)
        assert json.loads(payload)["nom"] == "Modifie"
        print("All API checks passed.")
    finally:
        check("DELETE", "/etudiants/I999", 204)
        check("DELETE", "/options/99", 204)
        check("DELETE", "/options/99", 404)


if __name__ == "__main__":
    main()
