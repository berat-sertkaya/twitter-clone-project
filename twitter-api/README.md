# Twitter API (Spring Boot + PostgreSQL)

## Çalıştırma
1. PostgreSQL'de veritabanı oluştur: `CREATE DATABASE twitter_db;`
2. `src/main/resources/application.properties` içindeki kullanıcı/şifreyi kendine göre düzenle.
3. `mvn spring-boot:run` (veya IDE'den `TwitterApiApplication`) -> http://localhost:3000
4. Testler: `mvn test`

## Kimlik doğrulama
HTTP Basic. Önce `/register`, sonra diğer isteklerde `Authorization: Basic base64(username:password)`.
`GET /tweet/**` herkese açıktır (React CORS demosu için).

## Örnek istekler
```
curl -X POST localhost:3000/register -H "Content-Type: application/json" -d '{"username":"berat","email":"berat@mail.com","password":"123456"}'
curl -u berat:123456 -X POST localhost:3000/login
curl -u berat:123456 -X POST localhost:3000/tweet -H "Content-Type: application/json" -d '{"content":"Ilk tweetim","location":"Ankara"}'
curl "localhost:3000/tweet/findByUserId?userId=1"
curl "localhost:3000/tweet/findById?id=1"
curl -u berat:123456 -X PUT localhost:3000/tweet/1 -H "Content-Type: application/json" -d '{"content":"Guncel"}'
curl -u berat:123456 -X DELETE localhost:3000/tweet/1
curl -u berat:123456 -X POST localhost:3000/comment -H "Content-Type: application/json" -d '{"tweetId":1,"text":"Guzel"}'
curl -u berat:123456 -X PUT localhost:3000/comment/1 -H "Content-Type: application/json" -d '{"text":"Duzenlendi"}'
curl -u berat:123456 -X DELETE localhost:3000/comment/1
curl -u berat:123456 -X POST localhost:3000/like -H "Content-Type: application/json" -d '{"tweetId":1}'
curl -u berat:123456 -X POST localhost:3000/dislike -H "Content-Type: application/json" -d '{"tweetId":1}'
curl -u berat:123456 -X POST localhost:3000/retweet -H "Content-Type: application/json" -d '{"tweetId":1}'
curl -u berat:123456 -X DELETE localhost:3000/retweet/2   # 2 = retweet'in kendi id'si
```

## CORS
React (http://localhost:3200) -> API (http://localhost:3000) farklı origin olduğu için tarayıcı isteği engeller.
Çözüm: `SecurityConfig.corsConfigurationSource()` + `.cors(Customizer.withDefaults())`.
Hatayı görmek için `.cors(...)` satırını yorum satırı yapıp backend'i yeniden başlat.
