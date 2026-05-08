package com.krce;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class FakeAPITest {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://api.escuelajs.co/api/v1";
    }

    @Test
    public void testGetProducts() {
        RestAssured.given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThan(0));


    }

    @Test
    public void testFilterProductsByTitle() {
        RestAssured.given()
                .queryParam("title", "Classic Blue Baseball Cap")
                .when()
                .get("/products/")
                .then()
                .statusCode(200)
                .body("[0].title", Matchers.equalTo("Classic Blue Baseball Cap"));
    }

    @Test
    public void testFilterProductsByPriceRange() {
        RestAssured.given()
                .queryParam("price_min", 100)
                .queryParam("price_max", 500)
                .when()
                .get("/products/")
                .then()
                .statusCode(200)
                .body("price", Matchers.everyItem(Matchers.greaterThanOrEqualTo(100)))
                .body("price", Matchers.everyItem(Matchers.lessThanOrEqualTo(500)));

    }

    @Test
    public void testFilterProductsByCategory() {
        RestAssured.given()
                .queryParam("categorySlug", "clothes")
                .when()
                .get("/products/")
                .then()
                .statusCode(200)
                .body("category.slug", Matchers.everyItem(Matchers.equalTo("clothes")));
    }



    @Test
    public void testGetCategories() {
        RestAssured.given()
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .body("$", Matchers.instanceOf(List.class));
    }

    @Test
    public void testGetCategoriesById() {
        RestAssured.given()
                .pathParam("id", 1)
                .when()
                .get("/categories/{id}")
                .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(1));
    }
    @Test
    public void testCreateCategories(){
        String name = "Akila_" + System.currentTimeMillis();
        String body = """
                {
                    "name": "%s",
                    "image": "https://placeimg.com/640/480/any"
                }
                """.formatted(name);
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/categories")
                .then()
                .log().all()
                .statusCode(201)
                .body("name",Matchers.equalTo(name))
                .body("image",Matchers.equalTo("https://placeimg.com/640/480/any"));
    }

}