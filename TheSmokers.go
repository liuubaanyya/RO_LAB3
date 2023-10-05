package main

import (
	"fmt"
	"sync"
)

const (
	NUM_KURCIA = 3
)

var (
	tobacco = make(chan bool, 1)
	paper   = make(chan bool, 1)
	match   = make(chan bool, 1)
	wg      sync.WaitGroup
)

func agent() {
	for {
		// Посередник кладе на стіл два різних випадкових компонента.
		tobacco <- true
		paper <- true
		match <- true
		fmt.Println("Посередник поставив компоненти на стіл.")
		<-tobacco
		<-paper
		<-match
	}
}

func smokerTobacco() {
	for {
		<-tobacco
		fmt.Println("Курець з тютюном бере компоненти зі столу, скручує цигарку і курить.")
		wg.Done()
	}
}

func smokerPaper() {
	for {
		<-paper
		fmt.Println("Курець з папіром бере компоненти зі столу, скручує цигарку і курить.")
		wg.Done()
	}
}

func smokerMatch() {
	for {
		<-match
		fmt.Println("Курець з сірниками бере компоненти зі столу, скручує цигарку і курить.")
		wg.Done()
	}
}

func main() {
	wg.Add(NUM_KURCIA)
	go agent()
	go smokerTobacco()
	go smokerPaper()
	go smokerMatch()
	wg.Wait()
}
