;;;;
;;;; Prekode til innlevering 2a i IN2040 (H19): Prosedyrer for å jobbe med
;;;; Huffman-trær, fra SICP, Seksjon 2.3.4.
;;;;

;;; Merk at koden under gjør bruk av diverse innebygde kortformer for
;;; kjeder av car og cdr. F.eks er (cadr x) det samme som (car (cdr x)), 
;;; og (caadr x) tilsvarer (car (car (cdr x))), osv. 



;;;
;;; Abstraksjonsbarriere:
;;;

(define (make-leaf symbol weight)
  (list 'leaf symbol weight))

(define (leaf? object)
  (eq? (car object) 'leaf))

(define (symbol-leaf x) (cadr x))

(define (weight-leaf x) (caddr x))

(define (make-code-tree left right)
  (list left
        right
        (append (symbols left) (symbols right))
        (+ (weight left) (weight right))))

(define (left-branch tree) (car tree))

(define (right-branch tree) (cadr tree))

(define (symbols tree)
  (if (leaf? tree)
      (list (symbol-leaf tree))
      (caddr tree)))

(define (weight tree)
  (if (leaf? tree)
      (weight-leaf tree)
      (cadddr tree)))

;;;
;;; Dekoding:
;;;



(define (decode bits tree)
  (define (decode-1 bits current-branch res)
    (if (null? bits)
        (reverse res)
        (let ((next-branch(choose-branch (car bits) current-branch)))
          (if (leaf? next-branch)
              (decode-1 (cdr bits) tree (cons(symbol-leaf next-branch) res))
              (decode-1 (cdr bits) next-branch res)))))
  (decode-1 bits tree '()))

(define (choose-branch bit branch)
  (if (= bit 0) 
      (left-branch branch)
      (right-branch branch)))

(define (select-bit sym branch)
  (if (memq sym (right-branch branch))
      1
     0))




(define (encode symboler tree)
  (define (encode-1 symboler current-branch lst)

    (define (finn-bit sym branch l)
      (if (leaf? branch)
          (encode-1 (cdr symboler) tree l)
          (if (memq sym (left-branch branch))
              (finn-bit sym (left-branch branch) (cons 0 l)) ;vi skal til venstre
              (finn-bit sym (right-branch branch)(cons 1 l)) ;vi skal til høyre
              )))
          
    (if (equal? symboler '())
        (reverse lst)
        (finn-bit (car symboler) current-branch lst)))
  
  (encode-1 symboler tree '()))

#|


? (define codebook (grow-huffman-tree freqs))
? (decode (encode '(a b c) codebook) codebook) → (a b c)

(define (make-code-tree left right)
  (list left
        right
        (append (symbols left) (symbols right))
        (+ (weight left) (weight right))))
|#

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;HUFFMAN;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define (nth indeks lst)
  (if (equal? '() lst)
      '()
      (if (zero? indeks)
       (car lst)
       (nth (- indeks 1) (cdr lst)))))




(define (grow-huffman-tree symboler)
  
  (define set (make-leaf-set symboler))
  (let ((adj-set  (adjoin-set (car set) (cdr set))))




  ;;;hjelpeprod
  (define (grow-huffman-tree-1 tree sym)
    ;(display (make-code-tree (car adj-set) (cadr adj-set)))
   ; (display (nth (- (length tree) 1) tree))
    
    (if (null? sym)
       #t
       ;(if (<= (weight (car adj-set))(weight(cadr adj-set)))
       (make-code-tree (car adj-set) (cadr adj-set))
        ;   #f)
       ))  
  (grow-huffman-tree-1 '() adj-set)))


;;;;;;;;;;;;;;;;;;;;;;;;;codeword;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  ;summerer liste
  (define (sum elemList)
  (if
    (null? elemList)
    0
    (+ (car elemList) (sum (cdr elemList)))
  )
)

 ;finner antall bit for ett symbol
    (define (finn-bit sym branch lst)
      (if (leaf? branch)
          (reverse lst)
          (if (memq sym (left-branch branch))
              (finn-bit sym (left-branch branch) (cons 0 lst)) ;vi skal til venstre
              (finn-bit sym (right-branch branch)(cons 1 lst)) ;vi skal til høyre
              )))



;hovedprod
(define (expected-codeword-length tree)

  ;gir liste med vekt
  (define(find-total-weight branch liste)
    (if(leaf? branch)
               (sum (cons (weight-leaf branch) liste))
               (if(leaf? (left-branch tree))
                  (find-total-weight (right-branch branch)(cons (weight-leaf (left-branch branch)) liste))
                  (find-total-weight (left-branch branch)(cons (weight-leaf (left-branch branch)) liste)))))

  ;hjelpeprod
  (define (expected-codeword-length-1 next-symbol tree lst)
    
    (let ((total-weight (find-total-weight tree '())))
     (if (= (length next-symbol) 0)
         (sum lst)
         (expected-codeword-length-1 (cdr next-symbol)
                                     tree
                                     (cons (* (length (finn-bit (caar next-symbol) tree '()))
                                              (/ (cadr (car next-symbol)) total-weight)) lst))          
 
         )))
  
  (expected-codeword-length-1 (huffman-leaves tree) tree '()))


;;;;;;;;;;;;;;;;;;leaves;;;;;;;;;;;;;;;;;;;;;;;;


(define (huffman-leaves tree)
  
  (define(huffman-leaves-1 branch lst)


    (if(leaf? branch)
               (cons (list (symbol-leaf branch)(weight-leaf branch)) lst)
               (if(leaf? (left-branch tree))
                  (huffman-leaves-1 (right-branch branch)(cons (list (symbol-leaf (left-branch branch))(weight-leaf (left-branch branch))) lst))
                  (huffman-leaves-1 (left-branch branch)(cons (weight-leaf (left-branch branch)) lst)))))                       

  (huffman-leaves-1 tree '()))



;;;
;;; Sortering av node-lister:
;;;

(define (adjoin-set x set)
  (cond ((null? set) (list x))
        ((< (weight x) (weight (car set))) (cons x set))
        (else (cons (car set)
                    (adjoin-set x (cdr set))))))

(define (make-leaf-set pairs)
  (if (null? pairs)
      '()
      (let ((pair (car pairs)))
        (adjoin-set (make-leaf (car pair)
                               (cadr pair))
                    (make-leaf-set (cdr pairs))))))

;;;
;;; Diverse test-data:
;;;

;;listen blir sortert for oss, så vekten bestemmer hvor i treet elementet skal stå

(define sample-tree
  (make-code-tree 
   (make-leaf 'ninjas 8) 
   (make-code-tree 
    (make-leaf 'fight 5) 
    (make-code-tree 
     (make-leaf 'night 1) 
     (make-leaf 'by 1)))))





;KJØRING

(define sample-code '(0 1 0 0 1 1 1 1 1 0))


;(expected-codeword-length sample-tree)
;(display sample-tree)
(define freqs '((a 2) (b 5) (c 1) (d 3) (e 1) (f 3)))
(grow-huffman-tree freqs)

;(encode '(ninjas fight ninjas) sample-tree)
  
;(huffman-leaves sample-tree)

;(decode (encode '(ninjas fight ninjas by night) sample-tree) sample-tree)

