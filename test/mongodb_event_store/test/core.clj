(ns mongodb-event-store.test.core
  (:use mongodb-event-store.core
        [lazytest.describe
         :only [describe it given do-it using testing]]
        [lazytest.expect :only [expect]]
        somnium.congomongo
        helpers.uuid))

(mongo! :db :test)

(def append-events (partial append-events-fn default-config))

(def read-events (partial read-events-fn default-config))

(describe "An event-store"
  (given [id1 (uuid)
          id2 (uuid)
          aggregate-id (uuid)
          event1 {:_id id1
                  :_aggregate aggregate-id
                  :_number 1}
          event2 {:_id id2
                  :_aggregate aggregate-id
                  :_number 2}
          events [event1 event2]]
    (do-it "should can append the given events and read it from the events collection ordered ascending by their event number"
      (append-events events)
      (let [result (read-events aggregate-id)]
        (expect (= 2 (count result)))
        (expect (= #{id1 id2} (into #{} (map :_id result))))
        (expect (= [1 2] (map :_number result)))))
    (do-it "should can begin from a certain event number"
      (let [result (read-events aggregate-id 2)]
        (expect (= [2] (map :_number result)))))))
