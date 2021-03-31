package propagators

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import propagators.types.ClosedRangeData
import propagators.types.IntData

class TMSSpec : StringSpec({
    "Can hold multiple views" {
        TMSData(IntData).run {
            val tms = TMS(setOf(23.supported("foo"), 47.supported("bar")))
            tms.query("foo") shouldBe 23.supported("foo")
            tms.query("bar") shouldBe 47.supported("bar")
        }
    }

    "Querying for a view not held yields nothing" {
        TMSData(IntData).run {
            val tms = TMS(setOf(23.supported("foo"), 47.supported("bar")))
            tms.query("xyz") shouldBe null
            tms.query() shouldBe null
        }
    }

    "Querying for conflicting views yields nothing" {
        TMSData(IntData).run {
            val tms = TMS(setOf(23.supported("foo"), 47.supported("bar")))
            tms.query("foo", "bar") shouldBe null
        }
    }

    "Multiple views can combine" {
        TMSData(ClosedRangeData<Int>()).run {
            val tms = TMS<ClosedRange<Int>>(
                setOf(
                    (5..10).supported("foo"),
                    (3..8).supported("bar")
                )
            )
            tms.query("foo") shouldBe (5..10).supported("foo")
            tms.query("bar") shouldBe (3..8).supported("bar")
            val result = tms.query("foo", "bar")
            result shouldNotBe null
            result?.value?.start shouldBe 5
            result?.value?.endInclusive shouldBe 8
            result?.premises shouldBe setOf("foo", "bar")
        }
    }

    "Can obtain multiple views via merges" {
        TMSData(ClosedRangeData<Int>()).run {
            worldView = setOf("foo", "bar")
            val tms1 = TMS<ClosedRange<Int>>(setOf((6..10).supported("foo")))
            val tms2 = TMS<ClosedRange<Int>>(setOf((8..14).supported("bar")))
            val merged = tms1.merge(tms2)
            merged should beInstanceOf<MergeResult.Replaced<Supported<ClosedRange<Int>>>>()
            with (merged as MergeResult.Replaced<TMS<ClosedRange<Int>>>) {
                val foo = requireNotNull(value.query("foo"))
                foo.value.start shouldBe 6
                foo.value.endInclusive shouldBe 10
                foo.premises shouldContainExactlyInAnyOrder setOf("foo")

                val bar = requireNotNull(value.query("bar"))
                bar.value.start shouldBe 8
                bar.value.endInclusive shouldBe 14
                bar.premises shouldContainExactlyInAnyOrder setOf("bar")

                val foobar = requireNotNull(value.query("foo", "bar"))
                foobar.value.start shouldBe 8
                foobar.value.endInclusive shouldBe 10
                foobar.premises shouldContainExactlyInAnyOrder setOf("foo", "bar")
            }
        }
    }

    "Will remove views subsumed by others" {
        TMSData(ClosedRangeData<Int>()).run {
            worldView = setOf("foo", "bar")
            val tms1 = TMS<ClosedRange<Int>>(setOf((6..14).supported("foo")))
            val tms2 = TMS<ClosedRange<Int>>(setOf((8..10).supported("foo", "bar")))
            val merged = tms1.merge(tms2)
            merged should beInstanceOf<MergeResult.Replaced<Supported<ClosedRange<Int>>>>()
            with(merged as MergeResult.Replaced<TMS<ClosedRange<Int>>>) {
                value.query("foo") shouldBe null
            }
        }
    }
})
