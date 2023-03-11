#include "velox_task.h"

namespace trino::velox {

class TpchQ6DummyTask : public TpchQ6Task {
public:
   explicit TpchQ6DummyTask(){}

   void nextBatch(ArrowSchema* c_schema, ArrowArray* c_array) override {
      totalBatch--;
   }

   bool isFinished() override {
     return totalBatch == 0;
   }

   void close() override {
   }
private:
  int totalBatch{10};
};


std::shared_ptr<TpchQ6Task> TpchQ6Task::Make(){
   return std::make_shared<TpchQ6DummyTask>();
}

} //namespace trino::velox