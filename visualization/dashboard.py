import streamlit as st
import pandas as pd

df = pd.read_csv("C:/GraphX-Citation/output/graph_edges/part-00000", 
                 names=["src","dst"])

st.title("Citation Network Dashboard")

st.subheader("Top Cited Papers")
st.write(df["dst"].value_counts().head(20))

st.subheader("Top Referencing Papers")
st.write(df["src"].value_counts().head(20))

st.subheader("Total Papers:")
st.write(len(pd.unique(df[["src","dst"]].values.ravel())))
