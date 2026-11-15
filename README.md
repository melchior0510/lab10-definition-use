# Лаборатори №2: Гүйцэтгэлийн хэмжүүрийг k6-аар хэмжих

Оюутны нэр: Munkh-Orgil
Оюутны код: B242270130

## k6 version

k6 v2.2.0 (commit/00a9a1b7f5, go1.26.5, linux/amd64)

## Алхам 2: Baseline (5 VU)

Команд: k6 run --vus 5 --duration 1m script.js | tee results/run-05vu.txt

p95 = 330.17мс гарсан (avg 170.2мс, p90 275.6мс, throughput 7.22
req/s, checks 100%, алдаа 0%). Энэ тоог доор SLO бодоход ашиглав.

## Алхам 3: Ачааллыг шатлан өсгөх (5 / 30 / 100 VU)

Команд:
k6 run --vus 5   --duration 1m script.js | tee results/run-05vu.txt
k6 run --vus 30  --duration 1m script.js | tee results/run-30vu.txt
k6 run --vus 100 --duration 1m script.js | tee results/run-100vu.txt

Үр дүн:
- 5 VU: p90 275.6мс, p95 330.17мс, avg 170.2мс, 7.22 req/s, алдаа 0%
- 30 VU: p90 232.42мс, p95 453мс, avg 170.11мс, 43.44 req/s, алдаа 0%
- 100 VU: p90 232.8мс, p95 453.41мс, avg 171.15мс, 142.85 req/s, алдаа 0%

5-аас 30 VU болоход p95 нэлээд өссөн (330→453мс). Харин 30-аас 100
VU болоход бараг л өөрчлөгдөөгүй. Throughput нь гурвандаа тэгш
өссөн. Тэгэхээр latency нь 30 VU орчимд нэг удаа тодорхой шатлаад,
дараа нь тогтвортой байсан гэж харагдлаа.

## Алхам 4: Thresholds (SLO)

Baseline p95 (330.17мс) × 1.5 = 495.26 ≈ 495мс гэсэн SLO гарна.
1.5 сонгосон нь — жирийн хэлбэлзэл дээр threshold барихгүй, харин
чинээлэг доройтол гарвал л барих гэсэн санаа. script-threshold.js
файл дотор thresholds-ийг шууд кодод бичсэн (http_req_duration:
['p(95)<495']).

Команд: k6 run script-threshold.js | tee results/run-threshold-pass.txt

PASS: p95 = 461.48мс (avg 174.6мс, checks 100%, http_req_failed 0%).
495мс-ээс бага тул threshold давсангүй.

Команд: k6 run script-threshold-fail.js | tee results/run-threshold-fail.txt

FAIL: script-threshold-fail.js-д threshold-ыг зориуд 50мс болгосон.
Бодит p95 = 452.12мс гарсан тул мэдээж давсан (avg 170.86мс, checks
100%, http_req_failed 0%). ERRO[0062] "thresholds on metrics
'http_req_duration' have been crossed" гэсэн алдаа гарлаа. checks
нь бүгд 100% амжилттай, http_req_failed ч 0% байсан тул сервер
өөрөө хэвийн ажиллаж байгаа, зөвхөн threshold-оо хэт хатуу тавьсны
улмаас л FAIL гарсан.

script-stages.js-ийг ч ажиллуулж үзсэн (5→30→100 VU шатлан):

Команд: k6 run script-stages.js | tee results/run-stages.txt

p95 = 456.33мс гарсан (avg 171.78мс, throughput 44.41 req/s) — 30
VU-ийн тогтмол тестийн p95 (453мс)-тай бараг адилхан.

## Алхам 5: Локал сервер

Команд:
node server.js &
k6 run --vus 30 --duration 1m script-local.js | tee results/run-local-fast.txt
(дараа нь /slow-руу сольж дахин)
k6 run --vus 30 --duration 1m script-local.js | tee results/run-local-slow.txt

- /fast: avg 4.71мс, p90 9.75мс, p95 12.07мс, max 24.83мс, 29.68 req/s, алдаа 0%
- /slow: avg 113.87мс, p90 126.61мс, p95 135.94мс, max 206.23мс, 26.72 req/s, алдаа 0%

/slow дотор зориуд 100мс saatai код бичсэн байсан, тэр яг л p95 дээр
харагдлаа (12→136мс). Throughput бага зэрэг буурсан.

## Дүгнэлт

VU олшрох тусам latency нэмэгддэг гэдэг нь бага VU-ийн үед л
харагдсан (5→30-д 330-аас 453мс болж өссөн), 30-аас цааш бараг
тогтвортой байсан (453→453.41мс). Throughput харин VU-тэй хамт
шугаман өссөн (7.22→43.44→142.85 req/s). Server.js дотор зориуд
оруулсан 100мс saatai нь p95-д шууд тусгагдаж байсан тул хэмжилт
зөв ажиллаж байгааг харуулж байна. Threshold-ийг зөв утгаар (495мс)
тавихад PASS (461.48мс), хэтэрхий хатуу (50мс) тавихад FAIL
(452.12мс) гарсан нь k6-ийн threshold механизм зөв ажиллаж байгааг
батлав. FAIL тохиолдолд ч http_req_failed 0% байсан тул сервер бус
зөвхөн хугацааны шаардлага хатуу байснаас FAIL гарсан гэдэг
тодорхой.