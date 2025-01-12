/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * ViewModel containing all the logic needed to run the game
 */
class GameViewModel : ViewModel() {

    // current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String> get() = _word
    val wordHint = Transformations.map(word) { word ->

        // get random index
        val randomPosition = (1..word.length).random()

        // return formatted string
        "Current word has " + word.length + " letters" +
                "\nThe letter at position " + randomPosition + " is " + word.get(randomPosition - 1).toUpperCase()
    }

    // current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    // event: game over
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean> get() = _eventGameFinish

    // countdown timer
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long> get() = _currentTime
    val currentTimeString = Transformations.map(currentTime) { time -> DateUtils.formatElapsedTime(time) } // executed on main thread, be careful

    // The list of words - the front of the list is the next _word to guess
    private lateinit var wordList: MutableList<String>

    // timer
    private val timer: CountDownTimer

    // static properties and methods
    companion object {

        // Time when the game is over
        private const val DONE = 0L

        // Countdown time interval
        private const val ONE_SECOND = 1000L

        // Total time for the game
        private const val COUNTDOWN_TIME = 60000L
    }

    init {
        _word.value = ""
        _score.value = 0
        Log.i("GameViewModel", "GameViewModel created!")
        resetList()
        nextWord()

        // Creates a timer which triggers the end of the game when it finishes
        // need "object of type ..." since CountDownTimer is abstract
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished/ONE_SECOND
            }

            override fun onFinish() {
                _currentTime.value = DONE
                onGameFinish()
            }
        }

        timer.start()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Callback called when the ViewModel is destroyed
     */
    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
        timer.cancel() // clean up resources
    }

    /** Methods for updating the UI **/
    fun onSkip() {
        _score.value = (_score.value)?.minus(1)
        nextWord()
    }
    fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        nextWord()
    }

    /**
     * Moves to the next _word in the list.
     */
    private fun nextWord() {

        // Shuffle the word list, if the list is empty
        if (wordList.isEmpty()) resetList()

        // Select and remove a _word from the list
        else _word.value = wordList.removeAt(0)
    }

    /** Method for the game completed event **/

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    fun onGameFinish() {
        _eventGameFinish.value = true
    }

}
