(ns mongodb-event-store.core
  (:use somnium.congomongo))

(def default-config {:collection :events
                     :aggregate-id-key :_aggregate
                     :event-number-key :_number})

(defn append-events-fn [{:keys [collection]} events]
  (insert! collection events))

(defn read-events-fn [{:keys [collection aggregate-id-key event-number-key]} aggregate-id & [from-number]]
  (let [where {aggregate-id-key aggregate-id}
        where (if from-number
                (merge where {event-number-key {:$gte from-number}})
                where)]
    (fetch collection
           :where where
           :sort {event-number-key 1})))

